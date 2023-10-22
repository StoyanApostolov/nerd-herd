package com.nerd.herd.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.carbon.domain.CarbonInvoice;
import com.nerd.herd.carbon.service.CarbonInvoiceService;
import com.nerd.herd.cards.dto.AddressDTO;
import com.nerd.herd.cards.service.AddressService;
import com.nerd.herd.cards.service.InvoiceService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/trans")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TransController {

	private final InvoiceService invoiceService;
	private final AuthService authService;
	private final CarbonInvoiceService carbonInvoiceService;
	private final AddressService addressService;

	@PostMapping("/{id}")
	@CrossOrigin(origins = "*")
	public AddressDTO retrieveAddressWithACL(@PathVariable String id) {
			return addressService.getById(id);
		}

	@PostMapping("/generate/random")
	@CrossOrigin(origins = "*")
	public CarbonInvoice createRandom() throws Exception {
		return carbonInvoiceService.createRandomTransaction();
	}

	@GetMapping("/all/list")
	@CrossOrigin(origins = "*")
	public List<CarbonInvoice> listCarbon() throws Exception {
		return carbonInvoiceService.allInvoices().stream().filter(i ->
			i.getInvoiceRows() != null && !i.getInvoiceRows().isEmpty() &&
					i.getInvoiceRows().get(0) != null && i.getInvoiceRows().get(0).getProduct() != null).toList();
	}

	@GetMapping("/clean/stats")
	@CrossOrigin(origins = "*")
	public void cleanUpStats() throws Exception {
		carbonInvoiceService.cleanUpInvoicesAndStats();
	}

	@GetMapping("/one/{id}")
	@CrossOrigin(origins = "*")
	public CarbonInvoice findOneById(@PathVariable String id) throws Exception {
		return carbonInvoiceService.findOneById(id);
	}


}