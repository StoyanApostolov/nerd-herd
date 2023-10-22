package com.nerd.herd.cards.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.cards.data.CustomerRepository;
import com.nerd.herd.authentication.domain.AuthUser;
import com.nerd.herd.cards.domain.HyperAddress;
import com.nerd.herd.cards.domain.HyperCustomer;
import com.stripe.model.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;
	private final AuthService authService;

	public HyperCustomer getByCurrentUser() {
		return getByUserId(authService.getAuthUser().getId());
	}

	public HyperCustomer getByCurrentUserIfPresent() {
		try {
			return getByUserId(authService.getAuthUser().getId());
		} catch (IllegalStateException e){
			return null;
		}
	}

	public HyperCustomer getByUserId(final String userId) {
		return customerRepository
				.findFirstByUserId(userId)
				.orElseThrow(() -> new IllegalStateException("Customer not found for user " + userId));
	}

	public HyperCustomer create(final HyperAddress address) throws Exception {
		final AuthUser user = authService.getAuthUser();
		final Map<String, Object> params = new HashMap<>();
		params.put("email", user.getEmail());
		params.put("description", "HyperCloud user " + user.getUsername());
		params.put("address", address.toParams());
		params.put("metadata", Collections.singletonMap("userId", user.getId()));
		final Customer customer = Customer.create(params);
		final HyperCustomer hyperCustomer =
				buildHyperCustomer(customer.getId(), user.getId(), address.getId());
		return customerRepository.save(hyperCustomer);
	}

	private HyperCustomer buildHyperCustomer(
			final String remoteId,
			final String userId,
			final String addressId) {
		final HyperCustomer hyperCustomer = new HyperCustomer();
		hyperCustomer.setRemoteId(remoteId);
		hyperCustomer.setUserId(userId);
		hyperCustomer.setDefaultAddressId(addressId);
		final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		hyperCustomer.setCreatedAt(now);
		hyperCustomer.setUpdatedAt(now);
		return hyperCustomer;
	}

	public void setDefaultAddress(final String addressId) {
		final HyperCustomer customer = getByCurrentUser();
		customer.setDefaultAddressId(addressId);
		customer.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
		customerRepository.save(customer);
	}

	public void setDefaultCreditCard(final String creditCardId) {
		final HyperCustomer customer = getByCurrentUser();
		customer.setDefaultCardId(creditCardId);
		customer.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
		customerRepository.save(customer);
	}
}
