package com.nerd.herd.stats.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonRatingStat {
	private String userId;
	private String userName;
	private String yearMonth;
	private BigDecimal totalCo = new BigDecimal("0");
	private BigDecimal averageCoPerEurSpent = new BigDecimal("0");
}
