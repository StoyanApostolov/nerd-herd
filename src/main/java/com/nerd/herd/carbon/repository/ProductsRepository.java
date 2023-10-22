package com.nerd.herd.carbon.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nerd.herd.carbon.domain.Product;

public interface ProductsRepository extends MongoRepository<Product, String> {
	List<Product> findBySector(String sector);

//	List<Product> findByNameLikeAndCo2eTotalLt(String name, BigDecimal co2eTotal);
}
