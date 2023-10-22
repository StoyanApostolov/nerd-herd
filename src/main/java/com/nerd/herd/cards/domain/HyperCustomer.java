package com.nerd.herd.cards.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document("customer")
@CompoundIndex(name = "idx_unique_user_customer", def = "{ userId: 1, remoteId: 1 }", unique = true)
public class HyperCustomer {
	@Id
	private String id;
	private String userId;
	private String remoteId;
	private String defaultAddressId;
	private String defaultCardId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String acl;
}
