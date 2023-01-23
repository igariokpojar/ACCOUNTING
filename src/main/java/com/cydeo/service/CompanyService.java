package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CompanyService {

    CompanyDto findCompanyById(Long id);
    //for update?

    CompanyDto findCompanyByTitle(String Title);
    //for update?

    CompanyDto getCompanyByLoggedInUser();

    List<CompanyDto> getAllCompanies();

    List<CompanyDto> getFilteredCompaniesForCurrentUser();

//    CompanyDto create(CompanyDto companyDto);

//    CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException;

    Company activate(Long companyId);

    Company deactivate(Long companyId);

    boolean isTitleExist(String title, Long companyId);

    CompanyDto save(CompanyDto company);

    CompanyDto updateCompany(CompanyDto companyDto);
}
