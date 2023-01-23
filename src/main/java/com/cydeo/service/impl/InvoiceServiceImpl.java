package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.*;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.NotEnoughProductException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final InvoiceProductService invoiceProductService;
    private final CompanyService companyService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, InvoiceProductService invoiceProductService, CompanyService companyService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceProductService = invoiceProductService;
        this.companyService = companyService;
    }


    @Override
    public InvoiceDto findInvoiceById(long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
       InvoiceDto invoiceDto= mapperUtil.convert(invoice, new InvoiceDto());
      List<InvoiceProductDto> invoiceProductList= invoiceProductService.getInvoiceProductsOfInvoice(id);
       invoiceDto.setInvoiceProducts(invoiceProductList);

        return invoiceDto;
    }

    @Override
    public List<InvoiceDto> getAllInvoicesOfCompany(InvoiceType invoiceType) throws Exception {
        Company company = mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
        List<Invoice> PurchaseInvoicesList = invoiceRepository.findInvoicesByCompanyAndInvoiceTypeAndIsDeleted(company, invoiceType, false);

        return PurchaseInvoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDto.setTax(getTotalTaxOfInvoice(invoice.getId()));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
            invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()));
            invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
            return invoiceDto;
        }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());

    }

    @Override
    public List<InvoiceDto> getAllInvoicesByInvoiceStatus(InvoiceStatus status) {
        Company company = mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeleted(company, status, false);


        return invoiceList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            Long invoiceId = invoiceDto.getId();
            invoiceDto.setTax(getTotalTaxOfInvoice(invoiceId));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceId));
            invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
            invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()));

            return invoiceDto;
        }).collect(Collectors.toList());

    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) throws Exception {

        Long companyId = (companyService.getCompanyByLoggedInUser().getId());
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        Company company=mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());;
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setCompany(company);
        invoiceRepository.save(invoice);
        invoiceDto.setId(invoice.getId());
        return invoiceDto;
    }

    @Override
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        invoice.setClientVendor(updatedInvoice.getClientVendor());
        Invoice invoice1=invoiceRepository.save(invoice);
        return invoiceDto;
    }

    @Override
    @Transactional
    public void approve(Long invoiceId) throws NotEnoughProductException {
        Invoice invoice = invoiceRepository.findInvoiceById(invoiceId);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setDate(LocalDate.now());
        invoiceProductService.completeApprovalProcedures(invoiceId, invoice.getInvoiceType());
        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto printInvoice(Long invoiceId) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findInvoiceById(invoiceId), new InvoiceDto());
        invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
        invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()));
        invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
        invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
        return invoiceDto;
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        if (invoice.getInvoiceStatus().getValue().equals("Awaiting Approval")) {
            invoice.setIsDeleted(true);
            invoiceProductService.getInvoiceProductsOfInvoice(id).stream().
                    peek(invoiceProductDto -> invoiceProductService.delete(invoiceProductDto.getId())).collect(Collectors.toList());
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public List<InvoiceDto> getLastThreeInvoices() { //my changes ilhan
        Company company=mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
        return invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeletedOrderByDateDesc(company, InvoiceStatus.APPROVED, false)
                .stream()
                .limit(3)
                .map(each -> mapperUtil.convert(each, new InvoiceDto()))
                .peek(this::calculateInvoiceDetails)
                .collect(Collectors.toList());
    }

    private void calculateInvoiceDetails(InvoiceDto invoiceDto) {   // my changes ilhan

        invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()));
        invoiceDto.setPrice(getTotalPriceOfInvoice(invoiceDto.getId()).subtract(invoiceDto.getTax()));
        invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
    }

    @Override
    public BigDecimal getTotalPriceOfInvoice(Long invoiceId) { // Invoice tax+ Invoice price
        List<InvoiceProductDto> listOfInvoiceProducts = invoiceProductService.getInvoiceProductsOfInvoice(invoiceId);
        if (listOfInvoiceProducts != null) {
            BigDecimal price = listOfInvoiceProducts.stream().map(invoiceProduct -> {
                        BigDecimal priceOfProduct = invoiceProduct.getPrice();
                        Integer quantityOfProduct = invoiceProduct.getQuantity();
                        return priceOfProduct.multiply(BigDecimal.valueOf(quantityOfProduct));
                    }
            ).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal tax = getTotalTaxOfInvoice(invoiceId);
            BigDecimal total = tax.add(price);
            return total;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalTaxOfInvoice(Long invoiceId) { // Sum of the tax of the Invoice Product

        List<InvoiceProductDto> listOfInvoiceProducts = invoiceProductService.getInvoiceProductsOfInvoice(invoiceId);
        if (listOfInvoiceProducts != null) {

            return listOfInvoiceProducts.stream().map(invoiceProductDto -> {
                BigDecimal price = invoiceProductDto.getPrice();
                Integer quantityOfProduct = invoiceProductDto.getQuantity();
                price = price.multiply(BigDecimal.valueOf(quantityOfProduct));
                BigDecimal tax = BigDecimal.valueOf(invoiceProductDto.getTax()).divide(BigDecimal.valueOf(100));
                return price.multiply(tax);
            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getProfitLossOfInvoice(Long id) {

        List<InvoiceProductDto> listOfInvoiceProducts= invoiceProductService.getInvoiceProductsOfInvoice(id);
        if (listOfInvoiceProducts != null) {
           return listOfInvoiceProducts.stream()
                .map(InvoiceProductDto::getProfitLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean checkIfInvoiceExist(Long clientVendorId) {
        Company company=mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
        Integer count = invoiceRepository.countAllByCompanyAndClientVendor_Id(company, clientVendorId);
        return count >= 1;
    }


    public String InvoiceNo(InvoiceType invoiceType, Long companyId) {
        Long id = invoiceRepository.countAllByInvoiceTypeAndCompanyId(invoiceType, companyId);
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);
        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);
        }
        return InvoiceNo;
    }
      @Override
      public List<InvoiceDto> getAllInvoicesByInvoiceStatusAndMonth(InvoiceStatus status,Integer  month,Integer year){
        Company company = mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeletedAndMonth(company, status, false, month, year);

        return invoiceList.stream()
                .filter(invoice -> invoice.getInvoiceType().equals(InvoiceType.SALES))
                .map(invoice -> {
                    InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                    Long invoiceId = invoiceDto.getId();

                    invoiceDto.setTax(getTotalTaxOfInvoice(invoiceId));
                    invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceId));
                    invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
                    invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()));

                    return invoiceDto;
                }).collect(Collectors.toList());
      }

}


