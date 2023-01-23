package com.cydeo.my_accounting_project.integration;

import com.cydeo.dto.*;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.service.*;
import com.cydeo.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class InvoiceServiceImplTest {



    InvoiceDto invoiceDto;
    @Autowired
    InvoiceServiceImpl invoiceService;



    @BeforeEach
    public void initTest() throws Exception {

        invoiceDto= TestDocumentInitializer.getInvoice(InvoiceType.PURCHASE);
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto.setId(1L);
    }

    @Test
    @WithMockUser(username = "manager@greentech.com", roles = { "Manager" })
    public void shouldCreateInvoiceSuccessfully() throws Exception {

       InvoiceDto invoiceDto= invoiceService.getNewInvoice(InvoiceType.PURCHASE);

       assertNotNull(invoiceDto.getDate());

    }

    @Test
    @WithMockUser(username = "manager@greentech.com", roles = { "Manager" })
    public void shouldSaveAnInvoice(){

        InvoiceDto invoiceDtoResult= invoiceService.save(invoiceDto, InvoiceType.PURCHASE);
        Long id=invoiceDtoResult.getId();
        assertEquals(1L, invoiceDtoResult.getId());
    }

}
