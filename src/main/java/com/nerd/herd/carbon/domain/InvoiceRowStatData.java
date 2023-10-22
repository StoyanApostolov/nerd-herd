package com.nerd.herd.carbon.domain;

import java.time.LocalDate;

import com.nerd.herd.util.ReflectionUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvoiceRowStatData extends InvoiceRow {
	private String userId;
	private String userName;
	private LocalDate localDate = LocalDate.now();

	public InvoiceRowStatData(InvoiceRow invoiceRow, String userId, String userName){
		ReflectionUtils.copyProps(invoiceRow, this);
		setUserId(userId);
		setUserName(userName);
	}
}
