package com.cydeo.service.impl;

import com.cydeo.annotation.Activation;
import com.cydeo.dto.CompanyDto;

import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public CompanyDto findCompanyById(Long id){

        Company company = companyRepository.findById(id).get();

        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public CompanyDto findCompanyByTitle(String title) {

        Company company = companyRepository.findCompanyByTitle(title);

        return mapperUtil.convert(company,new CompanyDto());
    }

    @Override
    public CompanyDto getCompanyByLoggedInUser() {

        return securityService.getLoggedInUser().getCompany();

    }

    @Override
    public List<CompanyDto> getAllCompanies() {

        return companyRepository.findAll(Sort.by("title"))
                .stream()
                .filter(company -> company.getId() != 1) //Removes CYDEO from list
                .map(company -> mapperUtil.convert(company,new CompanyDto()))
                .collect(Collectors.toList());

    }

    @Override
    public List<CompanyDto> getFilteredCompaniesForCurrentUser() {

        if(securityService.getLoggedInUser().getRole().getId()==1L){
            return companyRepository.findAll().stream()
                    .filter(company -> company.getId() != 1)
                    .map(company -> mapperUtil.convert(company ,new CompanyDto()))
                    .filter(company -> company.getCompanyStatus().equals(CompanyStatus.ACTIVE))
                    .collect(Collectors.toList()); //takes care of ROOT
        }
        if(securityService.getLoggedInUser().getRole().getId()==2L){
            Long companyId = securityService.getLoggedInUser().getCompany().getId();
            return getAllCompanies().stream()
                    .filter(companyDto -> companyDto.getId().equals(companyId))
                    .collect(Collectors.toList());
        }
        return getAllCompanies();
        //Need to test - Lorraine wants to improve on this - whatever... >:-)

    }

//    @Override
//    public CompanyDto create(CompanyDto companyDto) {
//
//       return null;
//
//    }

//    @Override
//    public CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException {
//        return null;
//    }

    @Activation
    @Override
    public Company activate(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(); //future exception message?
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);
    return company;
    }
    @Activation
    @Override
    public Company deactivate(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(); //future exception message?
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);
    return company;
    }

    @Override
    public boolean isTitleExist(String title, Long companyId) {
        if(companyId!=null) {
             Company company= companyRepository.findById(companyId).get();
             if(company.getTitle().equalsIgnoreCase(title))
                return false;
            }
        return companyRepository.existsByTitle(title);
    }

    @Override
    public CompanyDto save(CompanyDto companyDto) {
        if(companyDto.getCompanyStatus()==null) companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        Company company=companyRepository.save(mapperUtil.convert(companyDto, new Company()));
        companyDto.setId(company.getId());
        return companyDto;
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {

        Company company = companyRepository.findById(companyDto.getId()).get();
        Company convertedCompany = mapperUtil.convert(companyDto, new Company());
        convertedCompany.setId(company.getId());
        convertedCompany.setCompanyStatus(company.getCompanyStatus());
        companyRepository.save(convertedCompany);

        return findCompanyById(companyDto.getId());
    }

}
