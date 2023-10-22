package com.nerd.herd.cards.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.cards.domain.VirtualCashTrans;

public interface UserBalanceRepository extends MongoRepository<VirtualCashTrans, String> {

}