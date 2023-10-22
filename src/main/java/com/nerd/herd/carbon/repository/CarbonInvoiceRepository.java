package com.nerd.herd.carbon.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.nerd.herd.carbon.domain.CarbonInvoice;

public interface CarbonInvoiceRepository extends MongoRepository<CarbonInvoice, String> {
	List<CarbonInvoice> findOneByUserId(String userId);
	List<CarbonInvoice> findByInvoiceDate(LocalDate localDate);

}

