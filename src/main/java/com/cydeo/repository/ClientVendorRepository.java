package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ClientVendorRepository extends JpaRepository<ClientVendor, Long> {
    Optional<ClientVendor> findById(Long id);
    List<ClientVendor> findAllByCompany(Company company);
    List<ClientVendor> findAllByCompanyAndClientVendorType(Company company, ClientVendorType clientVendorType);
    Optional<ClientVendor> findClientVendorById(Long id);
    ClientVendor findByClientVendorNameAndCompany(String companyName, Company actualCompany);
    boolean existsByClientVendorName(String name);
    boolean existsByClientVendorNameAndCompany(String name,Company company);
}