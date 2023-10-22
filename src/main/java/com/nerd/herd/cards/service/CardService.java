package com.nerd.herd.cards.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.cards.data.CustomerRepository;
import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.dto.CardDTO;
import com.nerd.herd.cards.data.CardRepository;
import com.nerd.herd.cards.domain.HyperCard;
import com.nerd.herd.cards.domain.HyperCustomer;
import com.nerd.herd.cards.dto.CardCreateRequest;
import com.nerd.herd.cards.dto.CardWithAddressDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.Token;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {
	private final CardRepository cardRepository;
	private final AuthService authService;
	private final CustomerRepository customerRepository;
	private final CustomerService customerService;
	private final AddressService addressService;

	public CardDTO getById(final String id) {
		return cardRepository
				.findById(id)
				.map(CardDTO::new)
				.orElseThrow(() -> new IllegalStateException("Credit card not found for ID " + id));
	}

	public List<CardDTO> list() {
		final HyperCustomer customer = customerService.getByCurrentUser();
		final String defaultCardId = customer.getDefaultCardId();
		return cardRepository
				.findByRemoteCustomerId(customer.getRemoteId())
				.stream()
				.map(CardDTO::new)
				.map(dto -> dto.markDefaultIfMatches(defaultCardId))
				.collect(Collectors.toList());
	}

	public boolean haveAny() {
		final String userId = authService.getAuthUser().getId();
		return customerRepository.findFirstByUserId(userId).map(HyperCustomer::getDefaultAddressId).isPresent();
	}

	public CardDTO create(CardCreateRequest createRequest) throws Exception {
		final HyperCustomer customer = customerService.getByCurrentUser();
		final CardDTO dto = createCardInStripe(createRequest, customer);
		customerService.setDefaultCreditCard(dto.getId());
		return dto;
	}

	public void changeDefaultById(final String id) throws Exception {
		final HyperCard card = cardRepository
				.findById(id)
				.orElseThrow(() -> new IllegalStateException("Card not found for ID " + id));
		setDefaultCardInStripe(card.getRemoteId());
		customerService.setDefaultCreditCard(card.getId());
	}

	private Customer setDefaultCardInStripe(final String cardStripeId) throws StripeException {
		final String customerId = customerService.getByCurrentUser().getRemoteId();
		final Customer customer = Customer.retrieve(customerId);
		return updateCustomerDefaultSource(customer, cardStripeId);
	}

	private CardDTO createCardInStripe(final CardCreateRequest createRequest, final HyperCustomer hyperCustomer)
			throws StripeException {
		// To be able to attach a credit card to a customer we need to retrieve the customer along with customer's
		// sources.
		final Customer customerWithSources = retrieveCustomerWithSources(hyperCustomer.getRemoteId());
		final Card card = (Card) customerWithSources
				.getSources()
				.create(Collections.singletonMap("source", createRequest.getToken()));
		updateCustomerDefaultSource(customerWithSources, card.getId());

		final HyperCard hyperCard = new HyperCard(card, createRequest.getToken());
		hyperCard.setName(createRequest.getCardHolderName());
		hyperCard.setRemoteCustomerId(hyperCustomer.getRemoteId());
		return new CardDTO(cardRepository.save(hyperCard));
	}

	private Customer updateCustomerDefaultSource(final Customer customer, final String cardStripeId)
			throws StripeException {
		return customer.update(Collections.singletonMap("default_source", cardStripeId));
	}

	private Customer retrieveCustomerWithSources(String customerId) throws StripeException {
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		return Customer.retrieve(customerId, retrieveParams, null);
	}

	public CardCreateRequest createTestBO(final CardDTO request) throws StripeException {
		Map<String, Object> card = new HashMap<>();
		card.put("number", request.getCardNumber());
		card.put("exp_month", request.getExpMonth());
		card.put("exp_year", request.getExpYear());
		card.put("cvc", request.getCvc());
		Map<String, Object> params = new HashMap<>();
		params.put("card", card);

		Token token = Token.create(params);
		CardCreateRequest cardDTO = new CardCreateRequest();
		cardDTO.setToken(token.getId());
		cardDTO.setCardHolderName(request.getName());
//		customerRepository.findByRemoteId()
//		return createCardInStripe(final CardCreateRequest createRequest, new HyperCustomer("Gosho"))
		return cardDTO;
	}

	public void setup(final CardWithAddressDTO dto) throws Exception {
		final AddressDTO address = dto.getAddress();
		if (customerService.getByCurrentUserIfPresent() != null) {
			throw new IllegalStateException("User setup already done.");
		}
		if(dto.getCard() == null) {
			dto.setCard(createTestBO(dto.getCardTMP()));
		}
		customerService.create(address);
		addressService.create(address);
		create(dto.getCard());
	}

}
