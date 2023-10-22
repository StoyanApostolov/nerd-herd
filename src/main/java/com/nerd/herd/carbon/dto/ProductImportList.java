package com.nerd.herd.carbon.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportList {


	//	"activity_id":"consumer_goods-type_meat_products_beef",
	private String activity_id;
	//			"id":"c334822e-44a8-4cea-b713-23212f8c8f8c",
	private String id;
	//			"name":"Meat products (beef)",
	private String name;
	//			"category":"Food/Beverages/Tobacco",
	private String category;

	//			"sector":"Consumer Goods and Services",


//	"co2e_total":1.408
	private double co2e_total;

//			"factor":1.408,
	private double factor;


	private String sector;
	private String source;
	private String source_link;
	private String source_dataset;
	private String region;
	private String region_name;
	private String description;
	private String unit_type;
	private String unit;
	private String source_lca_activity;


//			"id":"c334822e-44a8-4cea-b713-23212f8c8f8c",
//			"name":"Meat products (beef)",
//			"category":"Food/Beverages/Tobacco",
//			"sector":"Consumer Goods and Services",
//			"source":"EXIOBASE",
//			"source_link":"https://zenodo.org/record/5589597#.Yh9_Zi8w1ao",
//			"source_dataset":"EXIOBASE 3",
//			"uncertainty":null,
//			"year":2019,
//			"year_released":2021,
//			"region":"AT",
//			"region_name":"Austria",
//			"description":"Emission intensity of supply chain in EUR spend on: meat products (beef). Retrieved from the EXIOBASE v3.8.2 model outputs for products. These factors were calculated based on 2019 data. CO2 equivalent factors incorporate emissions from land use; constituent gases have not been included as they do not. The LCA boundaries of these factors are not defined by the source. These factors include effects of international trade.",
//			"unit_type":"Money",
//			"unit":"kg/eur",
//			"source_lca_activity":"unknown",
//			"data_quality_flags":[
//
//			],
//			"access_type":"public",
//			"supported_calculation_methods":[
//			"ar4",
//			"ar5"
//			],
//			"factor":1.408,
//			"factor_calculation_method":"ar5",
//			"factor_calculation_origin":"source",
//			"constituent_gases":{
//		"co2e_total":1.408,
//				"co2e_other":null,
//				"co2":null,
//				"ch4":null,
//				"n2o":null
//	}

}
