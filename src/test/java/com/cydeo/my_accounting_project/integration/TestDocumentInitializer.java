package com.cydeo.my_accounting_project.integration;

import com.cydeo.dto.*;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.enums.ProductUnit;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
public class TestDocumentInitializer {

    public static UserDto getUser(String role){
        return UserDto.builder()
                .id(1L)
                .firstname("John")
                .lastname("Smith")
                .phone("+1 (111) 111-1111")
                .password("Abc1")
                .confirmPassword("Abc1")
                .role(new RoleDto(1L,role))
                .isOnlyAdmin(false)
                .company(getCompany(CompanyStatus.ACTIVE))

                .build();
    }

    public static CompanyDto getCompany(CompanyStatus status){
        return CompanyDto.builder()
                .title("Test_Company")
                .website("www.test.com")
                .id(1L)
                .phone("+1 (111) 111-1111")
                .companyStatus(status)
                .address(new AddressDto())
                .build();
    }

    public static InvoiceDto getInvoice(InvoiceType invoiceType){
        return InvoiceDto.builder()
                .id(1L)
                .invoiceNo("p-001")
                .clientVendor(getClientVendor())
                .invoiceProducts(getInvoiceProductList())
                .invoiceType(invoiceType)
                .company(getCompany(CompanyStatus.ACTIVE))
                .tax(BigDecimal.TEN)
                .price(BigDecimal.TEN)
                .build();
    }

    public static ProductDto getProduct(){
        return ProductDto.builder()
                .id(1L)
                .category(new CategoryDto())
                .productUnit(ProductUnit.GALLON)
                .lowLimitAlert(5)
                .build();
    }

    public static InvoiceProductDto getInvoiceProduct(){
        return InvoiceProductDto.builder()
                .product(getProduct())
                .id(1L)
                .profitLoss(BigDecimal.TEN)
                .price(BigDecimal.TEN)
                .quantity(1)
                .tax(10)
                .total(BigDecimal.valueOf(11))
                .remainingQuantity(0)
                .build();
    }

    public static List<InvoiceProductDto> getInvoiceProductList(){
      List<InvoiceProductDto> listOfInvoiceProducts= new ArrayList<>();
      listOfInvoiceProducts.add(getInvoiceProduct());
      return listOfInvoiceProducts;
    }

    public static ClientVendorDto getClientVendor(){
        return ClientVendorDto.builder()
                .clientVendorName("Build-A-Bear")
                .address(getAddress())
                .clientVendorType(ClientVendorType.VENDOR)
                .company(getCompany(CompanyStatus.ACTIVE))
                .id(1L)
                .build();
    }

    public static AddressDto getAddress(){
        return AddressDto.builder()
                .id(1L)
                .addressLine1("1134 Street st")
                .addressLine2("happy")
                .city("London")
                .country("Canada")
                .state("VA")
                .zipCode("90210-1212")
                .build();
    }
}
