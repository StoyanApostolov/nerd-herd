package com.nerd.herd.carbon.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.nerd.herd.carbon.domain.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductsDAO {

	private final MongoTemplate mongoTemplate;

	public List<Product> findOneByName(String name, double carbonFootPrint) {

		final Query query = new Query();
		if (name != null) {
			query.addCriteria(Criteria.where("name").regex(name));
		}

		query.addCriteria(Criteria.where("co2eTotalDouble").lt(carbonFootPrint));

		return mongoTemplate.find(query, Product.class);
	}

}
