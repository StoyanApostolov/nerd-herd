package com.nerd.herd.cards.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nerd.herd.cards.dto.CardDTO;
import com.nerd.herd.cards.data.AddressRepository;
import com.nerd.herd.cards.data.CardRepository;
import com.nerd.herd.cards.data.InvoiceRepository;
import com.nerd.herd.cards.data.UserBalanceRepository;
import com.nerd.herd.cards.domain.HyperAddress;
import com.nerd.herd.cards.domain.HyperCard;
import com.nerd.herd.cards.domain.HyperCustomer;
import com.nerd.herd.cards.domain.LocalInvoice;
import com.nerd.herd.cards.domain.VirtualCashTrans;
import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.dto.CreateInvoiceDTO;
import com.nerd.herd.cards.dto.PageDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceItem;
import com.stripe.model.StripeError;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

	private final CustomerService customerService;
	private final InvoiceRepository invoiceRepository;
	private final UserBalanceRepository userBalanceRepository;
	private final CardRepository cardRepository;
	private final AddressRepository addressRepository;
	private final SequenceGenerator sequenceGenerator;
	private final VirtualCashService virtualCashService;

	public Page<LocalInvoice> pageInvoices(PageDTO dto) {
		final Sort sortBy = Sort.by(Sort.Order.desc("createdOn"));
		final PageRequest pageable = PageRequest.of(dto.getPage(), dto.getSize(), sortBy);
		return invoiceRepository.findAll(pageable);
	}

	public LocalInvoice getInvoice(String id) {
		return invoiceRepository.findOneById(id);
	}

	public LocalInvoice createInvoice(final CreateInvoiceDTO invoiceDTO, final String userId) throws Exception {

		Long userBalance = virtualCashService.getUserVirtualCash(userId);
		VirtualCashTrans virtualCashTrans = new VirtualCashTrans();
		if (userBalance > 0) {
			Long newBalance = Math.max(0, userBalance - invoiceDTO.getAmount());

			Long oldAmount = invoiceDTO.getAmount();
			invoiceDTO.setAmount(Math.max(0, invoiceDTO.getAmount() - userBalance));
			virtualCashTrans.setCents(Math.max(0, oldAmount - invoiceDTO.getAmount()) * -1);
			String vctDesc = "";
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

			if (invoiceDTO.getAmount() != 0) {
				vctDesc = "Partially paid ";
			}


			virtualCashTrans.setDescription(vctDesc);
			if (virtualCashTrans.getCents() != 0) {
				virtualCashTrans.setUserId(userId);
				userBalanceRepository.save(virtualCashTrans);
			}

			// already paid
			if (newBalance > 0) {
				final String invoiceId = generateInvoiceId("NH");
				LocalInvoice vcInvoice = createVirtualCashOnlyInvoice(
						invoiceId,
						virtualCashTrans.getCents()*(-1L),
						userId,
						invoiceDTO.getCurrency(),
						virtualCashTrans.getDescription()
																	 );
				return invoiceRepository.save(vcInvoice);
			}
		}

		LocalInvoice localInvoice = createInvoiceInStripe(invoiceDTO, customerService.getByUserId(userId));


		return invoiceRepository.save(localInvoice);
	}

	public LocalInvoice createInvoiceInStripe(
			final CreateInvoiceDTO invoiceDTO,
			final HyperCustomer customer) throws StripeException {
		final HyperCard card = getDefaultCreditCard(customer);
		final HyperAddress address = getDefaultAddress(customer);

		final Map<String, Object> lineParams = new HashMap<>();
		lineParams.put("customer", customer.getRemoteId());
		lineParams.put("amount", invoiceDTO.getAmount());
		lineParams.put("currency", invoiceDTO.getCurrency());
		lineParams.put("description", invoiceDTO.getDescription());

		// this thing here works like a shopping cart
		// we create lines and the we consolidate them via Invoice.create
		InvoiceItem.create(lineParams);

		final Map<String, Object> params = new HashMap<>();
		params.put("customer", customer.getRemoteId());
		params.put("auto_advance", true);
		params.put("description", invoiceDTO.getDescription());

		final Invoice invoice = Invoice.create(params);

		final Optional<StripeError> maybePaymentError = payInvoiceOrError(invoice);

		final Invoice invoiceRetrieved = retrieveInvoice(invoice.getId());
		final String invoiceId = generateInvoiceId("HA");
		final LocalInvoice localInvoice = new LocalInvoice(
				invoiceId,
				invoiceRetrieved,
				new CardDTO(card),
				new AddressDTO(address));
//		maybePaymentError.ifPresent(localInvoice::setStripePaymentError);
		return localInvoice;
	}

	public String generateInvoiceId(final String prefix) {
		final int year = LocalDateTime.now().getYear();
		final Long sequence = sequenceGenerator.next(String.format("invoice-%s-%d", prefix, year));
		return String.format("%s%d-%06d", prefix, year, sequence);
	}

	private HyperCard getDefaultCreditCard(final HyperCustomer customer) {
		final String defaultCardId = customer.getDefaultCardId();
		return cardRepository
				.findById(defaultCardId)
				.orElseThrow(() -> new IllegalStateException("Credit card not found for ID " + defaultCardId));
	}

	private HyperAddress getDefaultAddress(final HyperCustomer customer) {
		final String defaultAddressId = customer.getDefaultAddressId();
		return addressRepository
				.findById(defaultAddressId)
				.orElseThrow(() -> new IllegalStateException("Address not found for ID " + defaultAddressId));
	}

	public Optional<StripeError> payInvoiceOrError(final String id) throws Exception {
		LocalInvoice localInvoice = invoiceRepository
				.findById(id)
				.orElseThrow(() -> new Exception("Cannot find invoice: " + id));

		Invoice invoice = Invoice.retrieve(localInvoice.getRemoteId());
		final Optional<StripeError> maybePaymentError = payInvoiceOrError(invoice);

		localInvoice.applyInvoice(Invoice.retrieve(localInvoice.getRemoteId()));
		maybePaymentError.ifPresent(e -> localInvoice.setPaymentError(e.getMessage()));
		invoiceRepository.save(localInvoice);

		return maybePaymentError;
	}
	private Optional<StripeError> payInvoiceOrError(final Invoice invoice) {
		try {
			invoice.pay();
			return Optional.empty();
		} catch (StripeException ex) {
			LOGGER.error("Invoice payment error.", ex);
			return Optional.ofNullable(ex.getStripeError());
		}
	}

	private Invoice retrieveInvoice(String invoiceId) throws StripeException {
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("lines");
		retrieveParams.put("expand", expandList);
		return Invoice.retrieve(invoiceId, retrieveParams, null);
	}

	private LocalInvoice createVirtualCashOnlyInvoice(
			final String invoiceId,
			Long virtualCash,
			String userId,
			String currency,
			String description) {
		LocalInvoice virtualInvoice = new LocalInvoice();
		HyperCustomer customer = customerService.getByUserId(userId);
		final HyperCard card = getDefaultCreditCard(customer);
		final HyperAddress address = getDefaultAddress(customer);
		virtualInvoice.setInvoiceId(invoiceId);
		virtualInvoice.setAmount(0L);
		virtualInvoice.setCustomerId(customer.getId());
		virtualInvoice.setDescription(description);
		virtualInvoice.setCurrency(currency);
		virtualInvoice.setPaid(true);
		virtualInvoice.setCard(new CardDTO(card));
		virtualInvoice.setAddress(new AddressDTO(address));
		return virtualInvoice;
	}
}
