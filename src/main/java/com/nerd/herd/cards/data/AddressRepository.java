package com.nerd.herd.cards.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.cards.domain.HyperAddress;

public interface AddressRepository extends MongoRepository<HyperAddress, String> {
}
