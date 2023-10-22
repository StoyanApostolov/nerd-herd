package com.nerd.herd.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.carbon.service.CarbonInvoiceService;
import com.nerd.herd.cards.domain.LocalInvoice;
import com.nerd.herd.cards.dto.CreateInvoiceDTO;
import com.nerd.herd.cards.service.InvoiceService;
import com.stripe.model.StripeError;

//@RestController
//@RequestMapping("/trans")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {


	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private AuthService authService;

	@Autowired
	private CarbonInvoiceService carbonInvoiceService;

	@PostMapping("/create")
	@CrossOrigin(origins = "*")
	public LocalInvoice createTransaction(@RequestBody CreateInvoiceDTO createInvoiceDTO) throws Exception {
		return invoiceService.createInvoice(createInvoiceDTO, authService.getAuthUser().getId());
	}

	@PostMapping("/pay/{invoiceId}")
	@CrossOrigin(origins = "*")
	public Optional<StripeError> payTransaction(@PathVariable String invoiceId) throws Exception {
		return invoiceService.payInvoiceOrError(invoiceId);
	}

//	@PostMapping("/generate/random")
//	@CrossOrigin(origins = "*")
//	public CarbonInvoice createRandom() throws Exception {
//		return carbonInvoiceService.createRandomTransaction();
//	}
//
//	@GetMapping("/all/list")
//	@CrossOrigin(origins = "*")
//	public List<CarbonInvoice> listCarbon() throws Exception {
//		return carbonInvoiceService.allInvoices();
//	}

}
