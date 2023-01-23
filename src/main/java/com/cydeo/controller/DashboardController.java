package com.cydeo.controller;

import com.cydeo.client.CurrencyExchangeClient;
import com.cydeo.service.CompanyService;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final InvoiceService invoiceService;
    private final CompanyService companyService;
    private final CurrencyExchangeClient currencyExchangeClient;

    public DashboardController(DashboardService dashboardService, InvoiceService invoiceService, CompanyService companyService, CurrencyExchangeClient currencyExchangeClient) {
        this.dashboardService = dashboardService;
        this.invoiceService = invoiceService;
        this.companyService = companyService;
        this.currencyExchangeClient = currencyExchangeClient;
    }
    @GetMapping("/dashboard")
    public String navigateToDashboard(Model model) throws Exception{

        model.addAttribute("companyTitle",companyService.getCompanyByLoggedInUser().getTitle());
        model.addAttribute("summaryNumbers",dashboardService.getSummaryNumbers());
        model.addAttribute("invoices",invoiceService.getLastThreeInvoices());
        model.addAttribute("exchangeRates",currencyExchangeClient.getExchangeRates().getUsd());
        model.addAttribute("title","Cydeo Accounting Dashboard");

        return"dashboard";

    }
}
