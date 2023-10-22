package com.nerd.herd.cards.dto;

import lombok.Data;

@Data
public class CardCreateRequest {
	private String token;
	// The Stripe.js does not collect Card Holder Name so we need to manage it ourselves.
	private String cardHolderName;
}
