package com.nerd.herd.stats.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.stats.domain.CustomerStats;

public interface StatsRepository extends MongoRepository<CustomerStats, String> {

	 CustomerStats  findByUserIdAndProductType(String userId, String productType);

	List<CustomerStats>  findByUserIdIn(List<String> userIds);
}
