package com.nerd.herd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.cards.dto.CardCreateRequest;
import com.nerd.herd.cards.dto.CardDTO;
import com.nerd.herd.cards.dto.CardWithAddressDTO;
import com.nerd.herd.cards.service.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/card")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@PostMapping("/setup")
	@CrossOrigin(origins = "*")
	public void createCardWithACL(@RequestBody CardWithAddressDTO dto) throws Exception {
		cardService.setup(dto);
	}

	@PostMapping("/{id}")
	@CrossOrigin(origins = "*")
	public CardDTO retrieveCardWithACL(@PathVariable String id) {
		return cardService.getById(id);
	}

	@GetMapping("/list")
	@CrossOrigin(origins = "*")
	public List<CardDTO> listCardWithACL() {
		return cardService.list();
	}

	@PostMapping("/create")
	@CrossOrigin(origins = "*")
	public CardDTO createCardWithACL(@RequestBody final CardCreateRequest request) throws Exception {
		return cardService.create(request);
	}

	@PostMapping("/create/test")
	@CrossOrigin(origins = "*")
	public CardCreateRequest createCardWithACLBackendOnly(@RequestBody final CardDTO request) throws Exception {
		return cardService.createTestBO(request);
	}

	@GetMapping("/check")
	@CrossOrigin(origins = "*")
	public boolean checkCardWithACL() {
		return cardService.haveAny();
	}

	@PostMapping("/default/{id}")
	@CrossOrigin(origins = "*")
	public void changeDefaultCardWithACL(@PathVariable String id) throws Exception {
		cardService.changeDefaultById(id);
	}

}
