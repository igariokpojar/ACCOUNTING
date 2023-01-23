package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
@Table(name="invoice_products")
public class InvoiceProduct extends BaseEntity {
    private BigDecimal price;
    private BigDecimal profitLoss;
    private int quantity;
    private int remainingQuantity;
    private int tax;
    @ManyToOne(fetch= FetchType.LAZY, cascade= CascadeType.MERGE)
    private Invoice invoice;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
