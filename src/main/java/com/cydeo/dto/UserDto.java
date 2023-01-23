package com.cydeo.dto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    Long id;

    @NotBlank(message = "First Name is a required field. ")
    @Size(max = 50, min = 2, message = "First Name should be 2-50 characters long.")
    String firstname;

    @NotBlank(message = "Last Name is a required field.")
    @Size(max = 50, min = 2, message = "Last Name should be 2-50 characters long.")
    String lastname;

    @NotBlank (message = "A user with this email already exists. Please try with different email.")
    @Email(message = "Email is a required field.")
    String username;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" // (ex: +1 (957) 463-7174)
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "Phone number is required field and may be in any valid phone number format.")
    String phone;

    @NotBlank (message = "Password is a required field.")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,}",
            message = "Password should be at least 4 characters long and need to contain  : " +
                    "1 capital letter, " +
                    "1 small letter, " +
                    "1 special character or number, ")
    String password;

    @NotBlank (message = "Passwords should match.")
    String confirmPassword;

    @Valid
    @NotNull (message = "Please select a role")
    RoleDto role;

    @Valid
    @NotNull (message = "Please select a company")
    CompanyDto company;

    Boolean isOnlyAdmin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;

    }

    public void setPassword(String password) {
        this.password = password;
        checkConfirmPassword();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        checkConfirmPassword();
    }
    private void checkConfirmPassword() {
        if (password != null && !password.equals(confirmPassword)) {
            this.confirmPassword = null;
        }
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    public Boolean getIsOnlyAdmin() {
        return isOnlyAdmin;
    }

    public void setIsOnlyAdmin(Boolean isOnlyAdmin) {
        this.isOnlyAdmin = isOnlyAdmin;
    }
}