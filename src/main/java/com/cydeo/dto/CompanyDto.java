package com.cydeo.dto;

import com.cydeo.enums.CompanyStatus;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private Long id;

    @NotBlank(message = "Title is a required field.")
    @Size(max = 100, min = 2, message = "Title should be 2-100 characters long.")
    private String title;

    @NotBlank(message= "Phone Number is required field and may be in any valid phone number format.")
    @Pattern(regexp = "^\\d{11}$|^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone is required field and may be in any valid phone number format." )
    private String phone;

    @NotBlank
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",message = "Website should have a valid format.")
    private String website;

    @Valid
    private AddressDto address;

    private CompanyStatus companyStatus;


}
