package com.nerd.herd.carbon.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nerd.herd.authentication.service.AuthService;
import com.nerd.herd.carbon.domain.CarbonInvoice;
import com.nerd.herd.carbon.domain.InvoiceRow;
import com.nerd.herd.carbon.domain.InvoiceRowStatData;
import com.nerd.herd.carbon.domain.Product;
import com.nerd.herd.carbon.repository.CarbonInvoiceRepository;
import com.nerd.herd.cards.dto.CreateInvoiceDTO;
import com.nerd.herd.cards.service.InvoiceService;
import com.nerd.herd.stats.domain.CustomerStats;
import com.nerd.herd.stats.repository.StatsRepository;

/*
	Mocking stuff is the best
	 */
@Service
public class CarbonInvoiceService {

	@Autowired
	private ProductsService productsService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private AuthService authService;

	@Autowired
	private CarbonInvoiceRepository carbonInvoiceRepository;

	@Autowired
	private StatsRepository statsRepository;

	/*
	Mocking stuff is the best
	 */
	private String[] ALL_VENDORS = {"Lidl", "Fantastico", "Billa", "EVN", "Sofia transport"};

	private String[] CARBON_SCORE = {"low", "medium", "high"};
	public CarbonInvoice createRandomTransaction() throws Exception {
		// stats service  to calculate qki to4ki

		Integer randomVendor = ThreadLocalRandom.current().nextInt(0, ALL_VENDORS.length - 1);

		List<Product> allProducts = productsService.productsByVendor(ALL_VENDORS[randomVendor]);
		Integer randomAmountOfProducts = ThreadLocalRandom.current().nextInt(1, 10);
		Long totalAmount = 0L;

		CarbonInvoice carbonInvoice = new CarbonInvoice();
		CreateInvoiceDTO createInvoiceDTO = new CreateInvoiceDTO();
		createInvoiceDTO.setDescription("");
		List<InvoiceRow> invoiceProducts = new ArrayList<>();
		BigDecimal totalCarbon = new BigDecimal("0");
		CustomerStats stats = new CustomerStats(authService.getAuthUser().getId(), authService.getAuthUser().getUsername(), "TMPTYPE");



		for (int i = 0; i < randomAmountOfProducts; i++) {
			Integer randomProductI = ThreadLocalRandom.current().nextInt(0, allProducts.size() - 1);
			Integer randomProductQty = ThreadLocalRandom.current().nextInt(1, 7);

			Product randomProduct = allProducts.get(randomProductI);
			if(randomProduct.getSector().equals("Energy")){
				randomProductQty = randomProductQty*10;
			}
			if(randomProduct.getSector().equals("Transport")){
				Long randomTransportSinglePrice = (long) ThreadLocalRandom.current().nextInt(100, 7000);
				randomProduct.setSinglePrice(randomTransportSinglePrice);
			}
			if (!createInvoiceDTO.getDescription().contains(randomProduct.getName())) {
				createInvoiceDTO.setDescription(createInvoiceDTO.getDescription() + "\n" + randomProduct.getName());
			}

			Long rowPrice = randomProduct.getSinglePrice() * randomProductQty;
			totalAmount += rowPrice;

			// cents to bigDecimal
			BigDecimal bdAmount = new BigDecimal(randomProduct.getSinglePrice()).
					divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_EVEN);

			BigDecimal rowCarbonKgs = randomProduct.getCo2eTotal().multiply(
					bdAmount.multiply(new BigDecimal(randomProductQty)));
			totalCarbon = totalCarbon.add(rowCarbonKgs);

			InvoiceRow invoiceRow = new InvoiceRow();
			invoiceRow.setQuantity(randomProductQty);
			invoiceRow.setRowPrice(rowPrice);
			invoiceRow.setProduct(randomProduct);
			invoiceRow.setRowCarbonKgs(rowCarbonKgs);
			invoiceProducts.add(invoiceRow);

			accumulateStats(stats, new InvoiceRowStatData(invoiceRow, authService.getAuthUser().getId(),  authService.getAuthUser().getUsername()));
		}
		createInvoiceDTO.setAmount(totalAmount);
		carbonInvoice.setTotalCarbon(totalCarbon);

		createInvoiceDTO.setAmount(totalAmount);
		carbonInvoice.setFootPrintPoints(new BigDecimal(totalAmount).divide(new BigDecimal("7"), RoundingMode.HALF_DOWN).longValue());

		carbonInvoice.setInvoiceRows(invoiceProducts);

		carbonInvoice.setTotalPrice(totalAmount);
		carbonInvoice.setVendor(ALL_VENDORS[randomVendor]);
		carbonInvoice.setUserName(authService.getAuthUser().getUsername());
		String userId = authService.getAuthUser().getId();

		carbonInvoice.setUserId(userId);
		Integer randomCarbonScore = ThreadLocalRandom.current().nextInt(0, CARBON_SCORE.length - 1);
		carbonInvoice.setCarbonScore(CARBON_SCORE[randomCarbonScore]);


		carbonInvoiceRepository.save(carbonInvoice);
		return carbonInvoice;
	}


	public List<CarbonInvoice> allInvoices() throws Exception {
		return carbonInvoiceRepository.findAll();
	}


	public void calculateStats() {
		List<CarbonInvoice> todayInvoices = carbonInvoiceRepository.findByInvoiceDate(LocalDate.now());
		HashMap<String, List<InvoiceRowStatData>> userIdToInvoiceRows = new HashMap<>();
		for(CarbonInvoice carbonInvoice : todayInvoices) {
			List<InvoiceRowStatData> statData = new ArrayList<>();
			for (InvoiceRow invoiceRow : carbonInvoice.getInvoiceRows()) {
				statData.add(new InvoiceRowStatData(invoiceRow, carbonInvoice.getUserId(), carbonInvoice.getUserName()));
			}
			userIdToInvoiceRows.put(carbonInvoice.getUserId(), statData);
		}
		addUserStats(userIdToInvoiceRows);
	}

	public void addUserStats(HashMap<String, List<InvoiceRowStatData>> userIdToInvoiceRows) {
		List<String> userIds = userIdToInvoiceRows.keySet().stream().toList();
		if(userIds.isEmpty()){
			return;
		}
		List<CustomerStats> allCustomerStats = statsRepository.findByUserIdIn(userIds);

		Map<String, Map<String, CustomerStats>>  idProductTypeToStat = allCustomerStats.stream().collect(
			Collectors.toMap(CustomerStats::getUserId,  e -> {
			Map<String, CustomerStats> hashMap = new HashMap<>();
			hashMap.put(e.getProductType(), e);
			return hashMap;
		}));

		for(String userId : userIds) {
			Map<String, CustomerStats> prodIdToStat = idProductTypeToStat.computeIfAbsent(userId, s -> new HashMap<>());

			CustomerStats accumulated = prodIdToStat.computeIfAbsent("SYSTEM_ALL",
				st -> new CustomerStats(userId, userIdToInvoiceRows.get(userId).get(0).getUserName(), "SYSTEM_ALL"));
			for (InvoiceRowStatData invoiceRow : userIdToInvoiceRows.get(userId)) {
				accumulateStats(accumulated, invoiceRow);
			}
			for (InvoiceRowStatData invoiceRow : userIdToInvoiceRows.get(userId)) {
				CustomerStats productTypeStat = prodIdToStat.computeIfAbsent(invoiceRow.getProduct().getType(),
					 st -> new CustomerStats(userId, invoiceRow.getUserName(), invoiceRow.getProduct().getType()));
				accumulateStats(productTypeStat, invoiceRow);
			}
			calculateCarbonPerEur(accumulated);
			prodIdToStat.values().forEach( st ->{
				calculatePartOfTotals(st, accumulated);
				calculateCarbonPerEur(st);
			});
			statsRepository.saveAll(prodIdToStat.values());
		}
	}


	private void accumulateStats(CustomerStats customerStats, InvoiceRowStatData row){
		customerStats.setTransactionsCount(customerStats.getTransactionsCount() + 1);
		customerStats.setTotalAmount(customerStats.getTotalAmount() + row.getProduct().getSinglePrice());
		customerStats.setCarbonTotal(customerStats.getCarbonTotal().add(row.getRowCarbonKgs()));
	}

	private void calculatePartOfTotals(CustomerStats customerStats, CustomerStats totals){
		customerStats.setPercentageOfAllTrans(
				customerStats.getCarbonTotal().divide(totals.getCarbonTotal(), BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100))
							 .setScale(2, BigDecimal.ROUND_HALF_EVEN));
	}

	private void calculateCarbonPerEur(CustomerStats customerStats) {
		customerStats.setAverageCarbonPerEur(
				(customerStats.getCarbonTotal().setScale(4, BigDecimal.ROUND_HALF_EVEN)).divide(
						new BigDecimal(customerStats.getTotalAmount()).divide(new BigDecimal(100)),BigDecimal.ROUND_HALF_EVEN));
	}

	public void cleanUpInvoicesAndStats() {
		statsRepository.deleteAll();
		carbonInvoiceRepository.deleteAll();
	}

	public CarbonInvoice findOneById(final String id) {
		return carbonInvoiceRepository.findById(id).orElseThrow(() ->  new IllegalStateException("No invoice found for: " + id ));
	}
}
