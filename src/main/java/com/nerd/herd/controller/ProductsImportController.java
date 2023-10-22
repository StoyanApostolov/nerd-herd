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

import com.nerd.herd.carbon.domain.CarbonInvoice;
import com.nerd.herd.carbon.domain.Product;
import com.nerd.herd.carbon.dto.ProductImportDTO;
import com.nerd.herd.carbon.service.ProductsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductsImportController {

	@Autowired
	private ProductsService productsService;

	@PostMapping("/import")
	@CrossOrigin(origins = "*")
	public void importProducts(@RequestBody ProductImportDTO productImportDTO) {
		productsService.importProducts(productImportDTO);
	}


	@GetMapping("/list")
	@CrossOrigin(origins = "*")
	public List<Product> listProducts() {
		return productsService.allProducts();
	}



	@GetMapping("/list/lower/{id}")
	@CrossOrigin(origins = "*")
	public List<Product> findLowerFootprint(@PathVariable String id) throws Exception {
		return productsService.findProductsWithLowerFootprint(id);
	}

}
