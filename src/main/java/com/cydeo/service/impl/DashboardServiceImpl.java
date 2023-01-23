package com.cydeo.service.impl;

import com.cydeo.client.CurrencyExchangeClient;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final CompanyService companyService;
    private final CurrencyExchangeClient client;

    public DashboardServiceImpl(InvoiceService invoiceService, CurrencyExchangeClient client, InvoiceRepository invoiceRepository, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.client = client;
        this.invoiceRepository = invoiceRepository;
        this.companyService = companyService;
    }

    public Map<String, BigDecimal> getSummaryNumbers()throws Exception{

        Map<String,BigDecimal> getSummaryNumbers = new HashMap<>();

        BigDecimal totalCost = invoiceService.getAllInvoicesByInvoiceStatus(InvoiceStatus.APPROVED).stream().filter(
                invoiceDto -> invoiceDto.getInvoiceType().equals(InvoiceType.PURCHASE))
                .map(InvoiceDto::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalSales = invoiceService.getAllInvoicesByInvoiceStatus(InvoiceStatus.APPROVED).stream().filter(
                        invoiceDto -> invoiceDto.getInvoiceType().equals(InvoiceType.SALES))
                .map(InvoiceDto::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal profitlossofsales = invoiceService.getAllInvoicesByInvoiceStatus(InvoiceStatus.APPROVED).stream().filter(
                invoiceDto -> invoiceDto.getInvoiceType().equals(InvoiceType.SALES))
                .map(InvoiceDto::getInvoiceProducts)
                .flatMap(Collection::stream)
                .map(InvoiceProductDto::getProfitLoss).reduce(BigDecimal.ZERO,BigDecimal::add);


                getSummaryNumbers.put("totalCost",totalCost);
                getSummaryNumbers.put("totalSales",totalSales);
                getSummaryNumbers.put("profitLoss",profitlossofsales);

        return getSummaryNumbers;
    }


//    public CurrencyDto getExchangeRates(){   //Tried Something
//
//        CurrencyDto currencyDto=new CurrencyDto();
//        currencyDto.setEuro(client.getExchangeRates().stream().filter(CurrencyDto ->currencyDto.getEuro()).collect(Collectors.toList()));
//        currencyDto.setBritishPound(client.getExchangeRates().getGbp());
//        currencyDto.setCanadianDollar(client.getExchangeRates().getCad());
//        currencyDto.setJapaneseYen(client.getExchangeRates().getJpy());
//        currencyDto.setIndianRupee(client.getExchangeRates().getInr());
//        return currencyDto;
//    }

}
