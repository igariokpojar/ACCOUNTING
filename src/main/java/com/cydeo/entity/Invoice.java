package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import javax.persistence.Entity;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "invoices")
//@Where(clause = "is_deleted=false")
public class Invoice extends BaseEntity {
    private String invoiceNo;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    private LocalDate date;
    @ManyToOne(fetch=FetchType.LAZY, cascade =  CascadeType.MERGE)
    private ClientVendor clientVendor;
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.MERGE) // Do we need to add Cascade?
    private Company company;
}
