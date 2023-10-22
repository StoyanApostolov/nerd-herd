package com.nerd.herd.cards.domain;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.dto.CardDTO;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceLineItem;
import com.stripe.model.StripeError;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document("invoice")
public class LocalInvoice {

	@Id
	private String id;
	private String invoiceId;
	private String remoteId;
	private String customerId;// customer
	// if true should charge the base source of the customer
	private Boolean autoAdvance;// auto_advance
	// Either charge_automatically, or send_invoice. When charging automatically,
	// Stripe will attempt to pay this invoice using the default source attached to
	// the
	private String collectionMethod;// collection_method
	private String description;
	private Date createdOn;
	private Date paidOn;
	// The number of days from when the invoice is created until it is due. Valid
	// only for invoices where collection_method=send_invoice.
	// private Integer daysUntilDue;//days_until_due
	private Date dueDate;
	private String currency;
	private Long amount;
	private Boolean paid;
	private String paymentError;

	private CardDTO card;
	private AddressDTO address;
	// Virtual cash
	private boolean refundedWithVC = false;


	public LocalInvoice(
			String invoiceId,
			Invoice invoice,
			CardDTO card,
			AddressDTO address) {
		setInvoiceId(invoiceId);
		applyInvoice(invoice);
		setCard(card);
		setAddress(address);
	}

	public void applyInvoice(Invoice invoice) {
		setRemoteId(invoice.getId());
		setCustomerId(invoice.getCustomer());
		setAutoAdvance(invoice.getAutoAdvance());
		setCollectionMethod(invoice.getCollectionMethod());
		setDescription(invoice.getDescription());
		setPaid(invoice.getPaid());
		Optional.ofNullable(invoice.getDueDate()).map(Date::new).ifPresent(this::setDueDate);
		Optional.ofNullable(invoice.getCreated()).map(Date::new).ifPresent(this::setCreatedOn);
		if (invoice.getLines() != null
				&& invoice.getLines().getData() != null
				&& invoice.getLines().getData().size() > 0) {
			InvoiceLineItem item = invoice.getLines().getData().get(0);
			setCurrency(item.getCurrency());
			setAmount(item.getAmount());
		}
	}


}
