package com.nerd.herd.cards.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("sequence")
public class Sequence {
	@Indexed
	private String id;
	private Long seq;
}
