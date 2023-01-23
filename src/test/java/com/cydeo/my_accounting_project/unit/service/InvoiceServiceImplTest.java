package com.cydeo.my_accounting_project.unit.service;

import com.cydeo.dto.*;
import com.cydeo.entity.*;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private MapperUtil mapperUtil;
    @Mock
    private InvoiceProductService invoiceProductService;
    @Mock
    private CompanyService companyService;
    InvoiceServiceImpl invoiceService;


    @BeforeEach
    public void setUp() {
        invoiceService = new InvoiceServiceImpl(invoiceRepository, mapperUtil, invoiceProductService, companyService);
    }

    @Test
    void givenAnInvoiceIdAnInvoiceIsReturnedWithTheSameId() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("P-001");
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setId(1L);

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());
        ArrayList<InvoiceProductDto> invoiceProducts = new ArrayList<>() {{

            add(new InvoiceProductDto() {{
                setPrice(BigDecimal.TEN);
                setQuantity(10);
                setTax(10);
            }});
            add(new InvoiceProductDto() {{
                setPrice(BigDecimal.TEN);
                setQuantity(10);
                setTax(10);
            }});
            add(new InvoiceProductDto() {{
                setPrice(BigDecimal.TEN);
                setQuantity(10);
                setTax(10);
            }});
        }};


        when(invoiceRepository.findInvoiceById(invoice.getId())).thenReturn(invoice);
        when(mapperUtil.convert(any(Invoice.class), any(InvoiceDto.class))).thenReturn(invoiceDto);
        when(invoiceProductService.getInvoiceProductsOfInvoice(invoice.getId())).thenReturn(invoiceProducts);
        invoiceDto.setInvoiceProducts(invoiceProducts);

        invoiceService.findInvoiceById(invoice.getId());

        assertEquals(1, invoiceDto.getId());
    }


    @Test
    void givenAnUpdatedInvoiceAnInvoiceIsReturnedWithTheUpdatedFields() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("P-001");
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setId(1L);
        invoice.setClientVendor(new ClientVendor("Staples", "234-234-2344", "www.staples.com", ClientVendorType.VENDOR
                , new Address(), new Company()));

        Invoice invoiceUpdated = new Invoice();
        invoiceUpdated.setInvoiceNo("P-001");
        invoiceUpdated.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceUpdated.setInvoiceType(InvoiceType.PURCHASE);
        invoiceUpdated.setId(1L);
        invoiceUpdated.setClientVendor(new ClientVendor("Walmart", "234-234-2344", "www.staples.com", ClientVendorType.VENDOR
                , new Address(), new Company()));

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto(1L, "Walmart", "234-234-2344", "www.staples.com", ClientVendorType.VENDOR
                , new AddressDto(), new CompanyDto()));


        when(mapperUtil.convert(any(InvoiceDto.class), any(Invoice.class))).thenReturn(invoiceUpdated);
        when(invoiceRepository.findInvoiceById(invoice.getId())).thenReturn(invoice);
        when(invoiceRepository.save(any())).thenReturn(invoiceUpdated);


        InvoiceDto invoiceDto1 = invoiceService.update(invoice.getId(), invoiceDto);

        assertEquals(invoiceDto.getClientVendor().getClientVendorName(), invoiceUpdated.getClientVendor().getClientVendorName());

        assertNotNull(invoiceDto1);

    }


    @Test
    void whenAnInvoiceIsApprovedItShouldNotBeDeleted() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("P-001");
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setId(1L);
        invoice.setClientVendor(new ClientVendor());
        invoice.setIsDeleted(false);

        when(invoiceRepository.findInvoiceById(invoiceDto.getId())).thenReturn(invoice);
        if (invoice.getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL)) {
            when(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId())).thenReturn(any());
            when(invoiceRepository.save(invoice)).thenReturn(invoice);
        }

        invoiceService.delete(1L);
        verify(invoiceRepository, never()).delete(any());
        verify(invoiceRepository, never()).save(isA(Invoice.class));
        assertEquals(false, invoice.getIsDeleted());
    }


    @Test
    void whenAnInvoiceIsAwaitingApprovalItShouldBeDeleted() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("P-001");
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setId(1L);
        invoice.setClientVendor(new ClientVendor());
        invoice.setIsDeleted(false);

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(2L);
        List<InvoiceProductDto> invoiceProductList = new ArrayList<>() {{
            add(invoiceProductDto);
        }};
        invoiceDto.setInvoiceProducts(invoiceProductList);

        when(invoiceRepository.findInvoiceById(invoiceDto.getId())).thenReturn(invoice);
        if (invoice.getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL)) {
            when(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId())).thenReturn(invoiceProductList);
            doNothing().when(invoiceProductService).delete(invoiceProductDto.getId());
            when(invoiceRepository.save(invoice)).thenReturn(invoice);

        }

        invoiceService.delete(1L);

        assertEquals(true, invoice.getIsDeleted());

    }

    @Test
    void whenPriceAndQuantityAreXTotalPriceOfInvoiceShouldBeY() {

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(2L);
        invoiceProductDto.setPrice(BigDecimal.valueOf(100L));
        invoiceProductDto.setTax(5);
        invoiceProductDto.setQuantity(10);

        InvoiceProductDto invoiceProductDto2 = new InvoiceProductDto();
        invoiceProductDto2.setId(3L);
        invoiceProductDto2.setPrice(BigDecimal.valueOf(110L));
        invoiceProductDto2.setTax(8);
        invoiceProductDto2.setQuantity(18);
        List<InvoiceProductDto> invoiceProductList = new ArrayList<>() {{
            add(invoiceProductDto);
            add(invoiceProductDto2);
        }};

        when(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId())).thenReturn(invoiceProductList);

        BigDecimal total = invoiceService.getTotalPriceOfInvoice(invoiceDto.getId());

        assertEquals(BigDecimal.valueOf(3188.40).setScale(2), total);

    }


    @Test
    void whenPriceAndQuantityAreXTotalTaxOfInvoiceShouldBeY() {

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(2L);
        invoiceProductDto.setPrice(BigDecimal.valueOf(100L));
        invoiceProductDto.setTax(5);
        invoiceProductDto.setQuantity(10);

        InvoiceProductDto invoiceProductDto2 = new InvoiceProductDto();
        invoiceProductDto2.setId(3L);
        invoiceProductDto2.setPrice(BigDecimal.valueOf(110L));
        invoiceProductDto2.setTax(8);
        invoiceProductDto2.setQuantity(18);
        List<InvoiceProductDto> invoiceProductList = new ArrayList<>() {{
            add(invoiceProductDto);
            add(invoiceProductDto2);
        }};

        when(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId())).thenReturn(invoiceProductList);

        BigDecimal total = invoiceService.getTotalTaxOfInvoice(invoiceDto.getId());

        assertEquals(BigDecimal.valueOf(208.40).setScale(2), total);

    }

    @Test
    void whenPriceAndQuantityAreXTotalProfitLossOfInvoiceShouldBeY() {

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-001");
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setInvoiceType(InvoiceType.SALES);
        invoiceDto.setId(1L);
        invoiceDto.setClientVendor(new ClientVendorDto());

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(2L);
        invoiceProductDto.setPrice(BigDecimal.valueOf(100L));
        invoiceProductDto.setTax(5);
        invoiceProductDto.setQuantity(10);
        invoiceProductDto.setProfitLoss(BigDecimal.valueOf(123));

        InvoiceProductDto invoiceProductDto2 = new InvoiceProductDto();
        invoiceProductDto2.setId(3L);
        invoiceProductDto2.setPrice(BigDecimal.valueOf(110L));
        invoiceProductDto2.setTax(8);
        invoiceProductDto2.setQuantity(18);
        invoiceProductDto2.setProfitLoss(BigDecimal.valueOf(345.45));
        List<InvoiceProductDto> invoiceProductList = new ArrayList<>() {{
            add(invoiceProductDto);
            add(invoiceProductDto2);
        }};

        when(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId())).thenReturn(invoiceProductList);

        BigDecimal total = invoiceService.getProfitLossOfInvoice(invoiceDto.getId());

        assertEquals(BigDecimal.valueOf(468.45).setScale(2), total);

    }
}
