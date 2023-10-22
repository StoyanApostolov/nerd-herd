package com.nerd.herd.carbon.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRow {
	private BigDecimal amount;
	private Integer quantity;
	private Long rowPrice;
	private Product product;
	private BigDecimal rowCarbonKgs;

}
