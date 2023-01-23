package com.cydeo.service;

import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {

   // List<UserDto> getAllFilterForLoggedInUser(UserDto loggedInUser);

    UserDto findUserById(Long id);
    UserDto findByUsername(String username);
    List<UserDto> getFilteredUsers() throws Exception;
    UserDto save(UserDto userDto);
    UserDto update(UserDto userDto);
    void delete(Long id);
    Boolean emailExist(UserDto userDto);

   // boolean userIsAdmin(UserDto userDto);

    // boolean isEmailExist(String username);

   // Boolean usernameExist(String username);

    // List<UserDto> listAllUsers();

    Boolean checkIfOnlyAdminForCompany(UserDto dto);


}
