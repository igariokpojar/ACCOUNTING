package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {



    InvoiceProduct findInvoiceProductById(Long productId);
    List<InvoiceProduct> findAllByInvoice(Invoice invoice);
    List<InvoiceProduct> findAllByInvoice_Id(Long id);
    List<InvoiceProduct> findAllByInvoice_InvoiceTypeAndInvoice_Company(InvoiceType invoiceType, Company company);
    List<InvoiceProduct> findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoice_DateDesc(InvoiceStatus invoiceStatus, Company company);
    List<InvoiceProduct> findInvoiceProductsByInvoiceInvoiceTypeAndProductAndRemainingQuantityNotOrderByIdAsc(InvoiceType invoiceType
            , Product product, Integer remainingQuantity);
    List<InvoiceProduct> findAllInvoiceProductByProductId(Long productId);
    List<InvoiceProduct> findAll();// Alehander Bessonov
    @Query("select i from InvoiceProduct  i where i.invoice.company.id = : id and i.invoice.invoiceStatus = 'APPROVED' "
    +"order by i.invoice.date desc")
    List<InvoiceProduct> findByInvoice_Company_IdAndInvoice_InvoiceStatusIsApprovedOrderByInvoice_DateDesc(@Param("id") Long companyId);
}