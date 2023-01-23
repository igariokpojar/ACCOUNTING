package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportingService {

    List<InvoiceProductDto> getStockData();

    Map<String, BigDecimal> getAllMonthlyProfitLossData(Integer month,Integer year) ;
    Map<String, BigDecimal> getProfitLossByMonth() ;

}
