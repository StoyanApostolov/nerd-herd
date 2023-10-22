package com.nerd.herd.cards.dao;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

import java.util.Optional;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.nerd.herd.cards.domain.Sequence;
import com.nerd.herd.cards.service.SequenceGenerator;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatabaseSequenceGenerator implements SequenceGenerator {
	private final MongoTemplate mongoTemplate;

	@Override
	public Long next(final String id) {
		final Query find = Query.query(Criteria.where("_id").is(id));
		final Update update = new Update().setOnInsert("_id", id).inc("seq", 1);
		final FindAndModifyOptions options = options().returnNew(true).upsert(true);
		return Optional
				.ofNullable(mongoTemplate.findAndModify(find, update, options, Sequence.class))
				.map(Sequence::getSeq)
				.orElse(1L);
	}
}
