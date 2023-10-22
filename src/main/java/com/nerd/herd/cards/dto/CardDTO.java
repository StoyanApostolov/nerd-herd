package com.nerd.herd.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nerd.herd.cards.domain.HyperCard;
import com.nerd.herd.util.ReflectionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CardDTO extends HyperCard {
	private String cardNumber;
	private String cvc;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Boolean defaultCard;

	/**
	 * Uniquely identifies this particular card number. You can use this attribute
	 * to check whether two customers who’ve signed up with you are using the same
	 * card number, for example. For payment methods that tokenize card information
	 * (Apple Pay, Google Pay), the tokenized number might be provided instead of
	 * the underlying card number.
	 */
	private String fingerprint;
	// Card funding type. Can be credit, debit, prepaid, or unknown.
	private String funding;

	// If a CVC was provided, results of the check: pass, fail, unavailable, or
	// unchecked.
	private String cvcCheck;

	public CardDTO(HyperCard card) {
		ReflectionUtils.copyNonNullProps(card, this);
	}

	public CardDTO markDefaultIfMatches(final String defaultCardId) {
		this.defaultCard = defaultCardId.equals(getId());
		return this;
	}
}
