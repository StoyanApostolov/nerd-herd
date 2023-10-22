package com.nerd.herd.cards.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document("pricing")
public class Pricing {

	private String id;
	private String service;
	private String target;
	private String metric;
	private String measure;
	private Long cents;
	private Long units;

	public Pricing(String measure, Long cents, Long units) {
		setMeasure(measure);
		setCents(cents);
		setUnits(units);
	}

	public String code() {
		return String.format("%s-%s-%s", getService(), getTarget() == null ? "" : getTarget(), getMetric());
	}

}
