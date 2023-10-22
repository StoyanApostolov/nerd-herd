package com.nerd.herd.carbon.domain;

import java.math.BigDecimal;
import java.util.Arrays;

import com.nerd.herd.carbon.dto.ProductImportList;
import com.nerd.herd.util.ReflectionUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
	private String id;
	private String name;
	private String carbonDbId;
	private String type;
	private String sector;
	private BigDecimal co2eTotal;
	private double co2eTotalDouble;
	private BigDecimal factor;
	// everything will be lev4e untill calculated otherwise
	private Long singlePrice = 100L;

	public Product(ProductImportList productImportList) {
		ReflectionUtils.copyPropsSkip(productImportList, this, Arrays.asList("id"));
		setCarbonDbId(productImportList.getId());
		setCo2eTotal(new BigDecimal(productImportList.getFactor()));
		setType(productImportList.getActivity_id());
	}
}
