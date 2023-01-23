package com.cydeo.service;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.enums.ClientVendorType;

import java.util.List;


public interface ClientVendorService {
    ClientVendorDto findClientVendorById(Long id);
    List<ClientVendorDto> getAllClientVendors() throws Exception;
    List<ClientVendorDto> getAllClientVendorsOfCompany(ClientVendorType clientVendorType);
    List<ClientVendorDto> getAllClientVendorsOfCompany(CompanyDto companyDto);
    ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception;
    ClientVendorDto update(Long id, ClientVendorDto clientVendorDto) throws ClassNotFoundException, CloneNotSupportedException;
    void delete(Long id) throws Exception;
    ClientVendorDto save(ClientVendorDto dto) throws Exception;
    boolean existsByNameAndCompany(String name, CompanyDto companyDto);
    List<ClientVendorType> getClientVendorType();


}