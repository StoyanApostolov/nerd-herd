package com.nerd.herd.cards.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document("virtual_cash")
public class VirtualCashTrans {
	@Id
	private String id;
	private String userId;
	private Long cents = 0L;
	// null for debit
	private String invoiceId;
	private String description;
}
