package com.nerd.herd.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.carbon.service.CarbonInvoiceService;
import com.nerd.herd.stats.domain.CustomerStats;
import com.nerd.herd.stats.repository.StatsRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StatsController {

	private final StatsRepository statsRepository;
	private final AuthService authService;
	private final CarbonInvoiceService carbonInvoiceService;



	@GetMapping("/list")
	@CrossOrigin(origins = "*")
	public List<CustomerStats> allStats() {
		return statsRepository.findAll();
	}


	@GetMapping("/clean")
	@CrossOrigin(origins = "*")
	public void deleteStats() {
		 statsRepository.deleteAll();
	}


	@GetMapping("/calculate")
	@CrossOrigin(origins = "*")
	public void calculateStats() {
		carbonInvoiceService.calculateStats();
	}
}
