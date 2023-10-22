package com.nerd.herd.cards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardWithAddressDTO {
	private CardDTO cardTMP;
	private CardCreateRequest card;
	private AddressDTO address;

}
