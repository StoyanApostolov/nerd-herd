package com.nerd.herd.cards.config;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PaymentProviderConfiguration {

	@Value("${stripe.sec.key:dummySecret}")
	private String secret;

	@PostConstruct
	void setup() {
		Stripe.apiKey = secret;
	}
}
