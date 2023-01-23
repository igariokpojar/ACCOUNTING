package com.cydeo.converter;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceProductService;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class InvoiceProductDtoConverter implements Converter<String, InvoiceProductDto> {
    InvoiceProductService invoiceProductService;

    public InvoiceProductDtoConverter(@Lazy InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @SneakyThrows
    @Override
    public InvoiceProductDto convert(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        return invoiceProductService.findInvoiceProductById(Long.parseLong(id));
    }
}