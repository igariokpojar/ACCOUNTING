package com.cydeo.converter;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.service.ClientVendorService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ClientVendorDtoConverter implements Converter<String, ClientVendorDto> {

    private final ClientVendorService clientVendorService;

    public ClientVendorDtoConverter(@Lazy ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @Override
    public ClientVendorDto convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        return clientVendorService.findClientVendorById(Long.parseLong(source));
    }
}
