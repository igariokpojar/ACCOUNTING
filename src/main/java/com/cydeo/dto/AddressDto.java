package com.cydeo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long id;
    @NotBlank(message = "Address is a required field.")
    @Size(max = 100, min = 2, message = "Address should have 2-100 characters.")
    private String addressLine1;
    @NotBlank(message = "Address is a required field.")
    @Size(max = 100, min = 2, message = "Address should have 2-100 characters.")
    private String addressLine2;
    @NotBlank(message = "City is a required field.")
    @Size(max = 58, min = 1, message = "City should be between 1-58 characters.")
    private String city;
    @NotBlank(message = "State is a required field.")
    private String state;
    @NotBlank(message = "Country is a required field.")
    private String country;
    @NotBlank(message = "Zip Code is required")
    @Pattern(regexp = "\\d{5}([ \\-]\\d{4})?$", message = "Zipcode should have a valid form.")
    private String zipCode;


}
