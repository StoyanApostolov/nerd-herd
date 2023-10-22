package com.nerd.herd.cards.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;

import javax.net.ssl.SSLContext;
import java.util.Objects;

@Configuration
public class MongoDBConfiguration extends SslConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConfiguration.class);

	@Lazy
	@Autowired
	private MongoTemplate mongoTemplate;
	@Lazy
	@Autowired
	private MongoMappingContext mongoMappingContext;

	@Bean
	@ConditionalOnProperty(
					value="spring.data.mongodb.skip-tls-validation",
					havingValue = "true",
					matchIfMissing = false
	)
	public MongoDatabaseFactory mongoDbFactorySsl(final MongoProperties mongoProperties) throws Exception {
		CodecRegistry registry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry());


		SSLContext sslContext = getSslContext();
		ConnectionString connectionString = new ConnectionString(mongoProperties.getUri());

		MongoClientSettings settings = MongoClientSettings.builder()
						.applyConnectionString(connectionString)
						.codecRegistry(registry)
						.applyToSslSettings(builder -> {
							builder.enabled(true);
							builder.context(sslContext);
						})
						.build();

		return new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), Objects.requireNonNull(connectionString.getDatabase()));
	}

	@Bean
	@ConditionalOnProperty(
					value="spring.data.mongodb.skip-tls-validation",
					havingValue = "false",
					matchIfMissing = true
	)
	public MongoDatabaseFactory mongoDbFactory(final MongoProperties mongoProperties) throws Exception {
		CodecRegistry registry = CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry());

		ConnectionString connectionString = new ConnectionString(mongoProperties.getUri());

		MongoClientSettings settings = MongoClientSettings.builder()
						.applyConnectionString(connectionString)
						.codecRegistry(registry)
						.build();

		return new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), Objects.requireNonNull(connectionString.getDatabase()));
	}


	@EventListener(ApplicationReadyEvent.class)
	void initIndexesAfterStartup() {
		LOGGER.info("Resolving MongoDB indexes...");
		final IndexResolver indexResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
		for (MongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
			final Class<?> clazz = persistentEntity.getType();
			if (clazz.isAnnotationPresent(Document.class)) {
				final IndexOperations indexOps = mongoTemplate.indexOps(clazz);
				indexResolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
			}
		}
		LOGGER.info("Indexes resolved.");
	}

}

