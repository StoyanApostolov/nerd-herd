package com.nerd.herd.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.service.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AddressController {
	private final AddressService addressService;

	@PostMapping("/{id}")
	@CrossOrigin(origins = "*")
	public AddressDTO retrieveAddressWithACL(@PathVariable String id) {
		return addressService.getById(id);
	}

	@GetMapping("/list")
	@CrossOrigin(origins = "*")
	public List<AddressDTO> listAddressWithACL() {
		return addressService.list();
	}

	@PostMapping("/create")
	@CrossOrigin(origins = "*")
	public AddressDTO createCustomerWithACL(@RequestBody AddressDTO addressDTO) {
		return addressService.create(addressDTO);
	}

	@GetMapping("/check")
	@CrossOrigin(origins = "*")
	public boolean checkAddressWithACL() {
		return addressService.hasAny();
	}

	@PostMapping("/default/{id}")
	public void changeDefaultAddressWithACL(@PathVariable String id) {
		addressService.changeDefaultById(id);
	}

}
