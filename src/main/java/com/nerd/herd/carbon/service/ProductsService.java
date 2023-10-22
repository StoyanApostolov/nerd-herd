package com.nerd.herd.carbon.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drew.lang.StringUtil;
import com.nerd.herd.carbon.dao.ProductsDAO;
import com.nerd.herd.carbon.domain.Product;
import com.nerd.herd.carbon.dto.ProductImportDTO;
import com.nerd.herd.carbon.dto.ProductImportList;
import com.nerd.herd.carbon.repository.ProductsRepository;

/**
 * mocking stuff is the best
 *
 */
@Service
public class ProductsService {

	private static String[] BRANDS_MOCK = {"base brand",    "cheap brand", "franchise brand", "local brand", "neighbouring brand"};
	private static String[] BRAND_MULTIPLIER_MOCK = {"1",       "1.55",             "1.1",         "0.5",                "0.7"};
	private static Long[] BRAND_VALUE_MOCK = {500L,       100L,       300L,         450L,                700L, 800L};

	private String[] CONSUMER_GOODS_VENDOR = {"Lidl", "Fantastico", "Billa"};
	private String[] ELECTRICITY_VENDOR = {"EVN"};

	private String[] TRANSPORT_VENDOR = {"Sofia transport"};

	@Autowired
	private ProductsRepository productsRepository;

	@Autowired
	private ProductsDAO productsDAO;

	public void importProducts(final ProductImportDTO productImportDTO) {
		productsRepository.deleteAll();
		List<Product> persistProducts = new ArrayList<>();
		for(ProductImportList productImportList : productImportDTO.getProductImportList()) {
			int i = 0;
			if(productImportList.getSector().equals("Consumer Goods and Services")) {
				for (String mockBrand : BRANDS_MOCK) {
					Product insertProduct = new Product(productImportList);

					if (insertProduct.getSector().equals("ne6toskypo")) {
						insertProduct.setSinglePrice(insertProduct.getSinglePrice() * 1975);
					}
					BigDecimal brandMultiplier = new BigDecimal(BRAND_MULTIPLIER_MOCK[i]);
					insertProduct.setCo2eTotal(
							insertProduct.getCo2eTotal().multiply(brandMultiplier));
					insertProduct.setName(mockBrand + " " + insertProduct.getName());
					persistProducts.add(insertProduct);
					i++;
				}
			} else {
				Product insertProduct = new Product(productImportList);
				if(insertProduct.getSector().equals("Energy")){
					insertProduct.setSinglePrice(40L);
				}
				persistProducts.add(insertProduct);
			}

		}
		productsRepository.saveAll(persistProducts);
	}

	public List<Product> allProducts() {
		return productsRepository.findAll();
	}

	public List<Product> productsByVendor(String vendor){
		if(Arrays.asList(CONSUMER_GOODS_VENDOR).contains(vendor)){
			return productsRepository.findBySector("Consumer Goods and Services");
		}
		if(Arrays.asList(TRANSPORT_VENDOR).contains(vendor)){
			return productsRepository.findBySector("Transport");
		}
		if(Arrays.asList(ELECTRICITY_VENDOR).contains(vendor)){
			return productsRepository.findBySector("Energy");
		}
		return productsRepository.findAll();
	}


	public void migrateProducts( ) {
		List<Product> products = productsRepository.findAll();
//		List<Product> migratedProducts = new ArrayList<>();
		for (Product product : products ) {
			product.setCo2eTotalDouble(product.getCo2eTotal().setScale(4, BigDecimal.ROUND_HALF_EVEN).doubleValue());
		}
		productsRepository.saveAll(products);
	}


	public List<Product> findProductsWithLowerFootprint(final String id) {
		migrateProducts();
		Product product = productsRepository.findById(id).orElseThrow(() ->
			new IllegalStateException("No product found for: " + id ));

		String nameWithoutBrand  = product.getName();
		for(String brand : BRANDS_MOCK) {
			nameWithoutBrand = nameWithoutBrand.replace(brand, "");
		}
		return productsDAO.findOneByName(nameWithoutBrand, product.getCo2eTotalDouble());
	}
}


// "activity_id":"restaurants_accommodation-type_hotel_restaurant_services",

// "activity_id":"electricity-supply_grid-source_market_for_electricity_high_voltage",

// "activity_id":"passenger_train-route_type_na-fuel_source_electricity",// "activity_id":"passenger_vehicle-vehicle_type_motor_vehicles_trailers_semitrailers-fuel_source_na-engine_size_na-vehicle_age_na-vehicle_weight_na",
//  "activity_id":"transport_services-type_railway_transportation_services",
// "activity_id":"electricity-supply_grid-source_market_for_electricity_high_voltage",

