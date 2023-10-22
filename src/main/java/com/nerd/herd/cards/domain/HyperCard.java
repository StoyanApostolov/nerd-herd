package com.nerd.herd.cards.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stripe.model.Card;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Document("creditCard")
public class HyperCard {

	@Id
	private String id;
	// card id in payment provider database
	private String remoteId;
	private String remoteCustomerId;
	// Cardholder name.
	private String name;
	// Card token in payment provider database - holds the data for a specific card,
	// used for requests
	private String token;
	private Long expMonth;
	private Long expYear;
	// Last 4 digits of the card
	private String last4;
	// Master card, visa, etc
	private String brand;


	public HyperCard(Card card, String token) {
		setName(card.getName());
		setBrand(card.getBrand());
		setToken(token);
		setExpMonth(card.getExpMonth());
		setExpYear(card.getExpYear());
		setLast4(card.getLast4());
		setRemoteId(card.getId());
		setExpMonth(card.getExpMonth());
		setExpYear(card.getExpYear());
	}
}
