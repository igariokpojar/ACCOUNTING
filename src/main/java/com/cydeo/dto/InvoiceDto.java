package com.cydeo.dto;

import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto{
    private Long id;
    private String invoiceNo;
    private InvoiceStatus invoiceStatus;
    private InvoiceType invoiceType;

    @DateTimeFormat(pattern="MMMM dd, yyyy")
    private LocalDate date;
    private CompanyDto company;
    @NotNull(message="This is a required field")
    private ClientVendorDto clientVendor;
    private BigDecimal price;                   //(only in Dto)
    private BigDecimal tax;                       //(only in Dto)
    private BigDecimal total;
    private List<InvoiceProductDto> invoiceProducts;
}