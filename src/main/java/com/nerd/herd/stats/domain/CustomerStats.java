package com.nerd.herd.stats.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStats {
	// gosho
	private String userId;
	private String userName;
	private Long transactionsCount = 0L;
	// cents
	private Long totalAmount = 0L;
	// 2022-10
	private String yearMonth;
	private String productType;
	private BigDecimal percentageOfAllTrans = new BigDecimal("0");
	private BigDecimal carbonTotal = new BigDecimal("0");
	private BigDecimal averageCarbonPerEur = new BigDecimal("0");


	public CustomerStats(String userId, String userName, String productType) {
		LocalDate now = LocalDate.now();
		setUserId(userId);
		setUserName(userName);
		setProductType(productType);
		setYearMonth(Integer.valueOf(now.getYear()).toString() + "-" + Integer.valueOf(now.getMonthValue()));
	}
}
