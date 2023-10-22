package com.nerd.herd.cards.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillingServiceDTO {
	private String metadata;
	private Long totalCents;
}
