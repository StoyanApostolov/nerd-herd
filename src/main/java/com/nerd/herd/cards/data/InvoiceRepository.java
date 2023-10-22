package com.nerd.herd.cards.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.cards.domain.LocalInvoice;

public interface InvoiceRepository extends MongoRepository<LocalInvoice, String> {

	LocalInvoice findOneById(String id);
	List<LocalInvoice> findByCustomerIdIn(List<String> customerIds);

}
