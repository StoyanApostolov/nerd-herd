package com.nerd.herd.cards.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.cards.domain.HyperCard;

public interface CardRepository extends MongoRepository<HyperCard, String> {
	List<HyperCard> findByRemoteCustomerId(String customerId);
}
