package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;
    private final EmailSenderService senderService;
    private final SecurityService securityService;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil, EmailSenderService senderService, SecurityService securityService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
        this.senderService = senderService;
        this.securityService = securityService;
    }


    @Override
    public ClientVendorDto findClientVendorById(Long id) {
        Optional<ClientVendor> clientVendor = clientVendorRepository.findClientVendorById(id);
        return mapperUtil.convert(clientVendor.get(), new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> getAllClientVendors() throws Exception {
        return clientVendorRepository.findAll()
                .stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .sorted(Comparator.comparing(ClientVendorDto::getClientVendorName))
                .collect(Collectors.toList());
    }


    @Override
    public ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception {
        return null;
    }


    @Override
    public List<ClientVendorDto> getAllClientVendorsOfCompany(ClientVendorType clientVendorType) {
        CompanyDto companyDto = securityService.getLoggedInUser().getCompany();
        Company company = mapperUtil.convert(companyDto, new Company());
        List<ClientVendor> clientVendorList = clientVendorRepository.findAllByCompanyAndClientVendorType(company, clientVendorType);
        return clientVendorList.stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .sorted(Comparator.comparing(ClientVendorDto::getClientVendorName))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> getAllClientVendorsOfCompany(CompanyDto companyDto) {

        List<ClientVendor> clientVendorList = clientVendorRepository.findAllByCompany(mapperUtil.convert(companyDto, new Company()));
        return clientVendorList.stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .sorted(Comparator.comparing(ClientVendorDto::getClientVendorName))
                .collect(Collectors.toList());
    }

//        @Override
//        public ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception {
//           ClientVendorDto newClientVendorDto = new ClientVendorDto();
//           ClientVendor newClientVendor = mapperUtil.convert(newClientVendorDto,new ClientVendor());
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
//            UserDto loggedInUser = userService.findByUsername(username);
//            clientVendorRepository.save(newClientVendor);
//            return findClientVendorById(newClientVendor.getId());
//        }

    @Override
    public ClientVendorDto update(Long id, ClientVendorDto clientVendorDto) throws ClassNotFoundException, CloneNotSupportedException {
        CompanyDto companyDto = securityService.getLoggedInUser().getCompany();
        Company company = mapperUtil.convert(companyDto, new Company());
        ClientVendor clientVendor = mapperUtil.convert(clientVendorDto, new ClientVendor());
        clientVendor.setCompany(company);
        clientVendorRepository.save(clientVendor);
        return findClientVendorById(clientVendorDto.getId());
    }

    @Override
    public ClientVendorDto save(ClientVendorDto dto) throws Exception {
        boolean nameExists = clientVendorRepository.existsByClientVendorName(dto.getClientVendorName());
        if (!nameExists) {
            CompanyDto companyDto = securityService.getLoggedInUser().getCompany();
            Company company = mapperUtil.convert(companyDto, new Company());
            ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());
            clientVendor.setCompany(mapperUtil.convert(companyDto, new Company()));

            //  clientVendor.setCompany(companyRepository.findById(dto.getCompany().getId()).get());
            clientVendorRepository.save(clientVendor);
            senderService.sendEmail("Notification", "<h1>Client/Vendor " + dto.getClientVendorName() +" was created</h1>");
            return findClientVendorById(clientVendor.getId());
        }
        return dto;
    }

    @Override
    public boolean existsByNameAndCompany(String name, CompanyDto companyDto) {
        Company company = mapperUtil.convert(companyDto,new Company());
        return clientVendorRepository.existsByClientVendorNameAndCompany(name,company);
    }

    @Override
    public void delete(Long id) throws Exception {
        Optional<ClientVendor> foundClientVendor = clientVendorRepository.findById(id);

        if (foundClientVendor.isPresent()) {
            foundClientVendor.get().setIsDeleted(true);
            clientVendorRepository.save(foundClientVendor.get());
        }
    }


    @Override
    public List<ClientVendorType> getClientVendorType() {
        return List.of(ClientVendorType.CLIENT, ClientVendorType.VENDOR);

    }


}