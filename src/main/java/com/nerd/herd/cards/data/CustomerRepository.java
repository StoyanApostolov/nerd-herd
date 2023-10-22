package com.nerd.herd.cards.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.cards.domain.HyperCustomer;

public interface CustomerRepository extends MongoRepository<HyperCustomer, String> {
	Optional<HyperCustomer> findFirstByUserId(String userId);
	// for testing
	List<HyperCustomer> findByUserId(String userId);

	Optional<HyperCustomer> findByRemoteId(String userId);
}
