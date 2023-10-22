package com.nerd.herd.carbon.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nerd.herd.cards.domain.LocalInvoice;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarbonInvoice {
	private String id;
	private String userId;
	private String userName;
	private String vendor;
	private BigDecimal totalCarbon;
	private Long footPrintPoints;
	private LocalDate invoiceDate = LocalDate.now();
	private Long totalPrice;
	private String carbonScore;
//	private LocalInvoice localInvoice;
	private List<InvoiceRow> invoiceRows;
}
