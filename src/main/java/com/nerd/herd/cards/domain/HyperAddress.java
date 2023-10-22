package com.nerd.herd.cards.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Document("billingAddress")
public class HyperAddress {

	@Id
	private String id;
	private String addressCity;
	private String addressCountry;
	// Address line 1 (Street address/PO Box/Company name).
	private String addressLine1;
	// Address line 2 (Apartment/Suite/Unit/Building).
	private String addressLine2;
	private String addressZip;

	private String companyName;
	private String companyVat;


	public Map<String, Object> toParams() {
		final Map<String, Object> params = new HashMap<>();
		params.put("city", addressCity);
		params.put("line1", addressLine1);
		params.put("line2", addressLine2);
		params.put("postal_code", addressZip);
		params.put("state", addressCountry);

		return params;
	}
}
