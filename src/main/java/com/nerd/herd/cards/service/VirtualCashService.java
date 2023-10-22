package com.nerd.herd.cards.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.nerd.herd.cards.data.UserBalanceRepository;
import com.nerd.herd.cards.domain.VirtualCashTrans;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VirtualCashService {

	private final UserBalanceRepository userBalanceRepository;
	private final MongoTemplate mongoTemplate;

	public VirtualCashTrans addVirtualCash(VirtualCashTrans addVirtualCashDTO) {
		return userBalanceRepository.save(addVirtualCashDTO);
	}

	public Long getUserVirtualCash(String userId) {
		Aggregation agg = newAggregation(
				match(Criteria.where("userId").is(userId)),
				group("userId").sum("cents").as("cents"));
		AggregationResults<VirtualCashTrans> aggregated = mongoTemplate.aggregate(agg, VirtualCashTrans.class,
																				  VirtualCashTrans.class);
		return aggregated.getMappedResults().size() > 0 ? aggregated.getMappedResults().get(0).getCents() : 0;
	}

	public Long getCurrentUserVirtualCash() {
		return getUserVirtualCash("TODO USER ID");
	}
}
