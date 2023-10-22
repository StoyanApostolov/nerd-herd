package com.nerd.herd.cards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateInvoiceDTO {

	private String description;
	private String currency = "USD";
	private Long amount;


	public CreateInvoiceDTO(ProFormDTO proFormDTO) {
		setAmount(proFormDTO.getAmount());
		setDescription(proFormDTO.getDescription());
		setCurrency(proFormDTO.getCurrency());
	}


}
