package com.nerd.herd.cards.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.cards.data.AddressRepository;
import com.nerd.herd.cards.data.CustomerRepository;
import com.nerd.herd.cards.domain.HyperAddress;
import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.domain.HyperCustomer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
	private final AddressRepository addressRepository;
	private final AuthService authService;
	private final CustomerService customerService;
	private final CustomerRepository customerRepository;

	public AddressDTO getById(final String id) {
		final HyperCustomer customer = customerService.getByCurrentUser();
		return addressRepository
				.findById(id)
				.map(AddressDTO::new)
				.map(dto -> dto.markDefaultIfMatches(customer.getDefaultAddressId()))
				.orElseThrow(() -> new IllegalStateException("Address not found for ID " + id));
	}

	public List<AddressDTO> list() {
		final HyperCustomer customer = customerService.getByCurrentUser();
		final String defaultAddressId = customer.getDefaultAddressId();
		return addressRepository
				.findAll()
				.stream()
				.map(AddressDTO::new)
				.map(dto -> dto.markDefaultIfMatches(defaultAddressId))
				.collect(Collectors.toList());
	}

	public boolean hasAny() {
		final String userId = authService.getAuthUser().getId();
		return customerRepository.findFirstByUserId(userId).map(HyperCustomer::getDefaultAddressId).isPresent();
	}

	public void changeDefaultById(final String id) {
		final HyperAddress address = addressRepository
				.findById(id)
				.orElseThrow(() -> new IllegalStateException("Address not found for ID " + id));
		customerService.setDefaultAddress(address.getId());
	}

	public AddressDTO create(final AddressDTO addressDTO) {
		final HyperAddress hyperAddress = addressDTO.toHyperAddress();
		final HyperAddress saved = addressRepository.save(hyperAddress);
		customerService.setDefaultAddress(saved.getId());
		return new AddressDTO(saved);
	}

}
