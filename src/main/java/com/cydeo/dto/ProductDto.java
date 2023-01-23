package com.cydeo.dto;
import com.cydeo.enums.ProductUnit;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductDto {

    private Long id;

    @NotNull(message = "Product Name is a required field.")
    @Size(max = 100, min = 2, message = "Product Name must between 2 & 100 characters long.")
    private String name;

    private Integer quantityInStock;

    @NotNull(message="Low Limit Alert is a required field")
    @Range(min=1, message="Low Limit should be at least 1.")
    private Integer lowLimitAlert;

    @Valid
    @NotNull(message = "Please select a Product Unit.")
    private ProductUnit productUnit;

    @Valid
    @NotNull(message = "Please select a Category.")
    private CategoryDto category;


}