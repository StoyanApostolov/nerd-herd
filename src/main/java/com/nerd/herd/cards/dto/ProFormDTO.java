package com.nerd.herd.cards.dto;

import java.util.HashMap;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProFormDTO {
	private Long amount;
	private String description;
	private String currency;
	private Integer invoiceDayOfMonth;
	private Integer invoiceMonth;

	public ProFormDTO(HashMap<String,Object> proform) {
		setAmount((long) (int) proform.get("amount"));
		setDescription((String) proform.get("description"));
		setCurrency((String) proform.get("currency"));
	}
}
