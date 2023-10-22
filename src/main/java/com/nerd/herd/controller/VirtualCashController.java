package com.nerd.herd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nerd.herd.cards.service.VirtualCashService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/virtual/cash")
@RequiredArgsConstructor
public class VirtualCashController {

	private final VirtualCashService virtualCashService;


	@GetMapping
	public Long getCurrentUserVirtualCash(){
		return virtualCashService.getCurrentUserVirtualCash();
	}
}
