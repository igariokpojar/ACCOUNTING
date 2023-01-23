package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService,
                           @Lazy SecurityService securityService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user, new UserDto());
    }


    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findUserById(id);
        UserDto dto = mapperUtil.convert(user, new UserDto());
        dto.setIsOnlyAdmin(checkIfOnlyAdminForCompany(dto));
        return dto;

    }

    @Override
    public List<UserDto> getFilteredUsers() {

        List<User> userList;
        if (isCurrentUserRootUser()) {
            userList = userRepository.findAllByRole_Description("Admin").stream()
                    .filter(user -> user.getCompany().getCompanyStatus().equals(CompanyStatus.ACTIVE)).collect(Collectors.toList());

        } else {
            userList = userRepository.findAllByCompany_Title(getCurrentUserCompanyTitle());
        }
        return userList.stream()
                .sorted(Comparator.comparing((User u) -> u.getCompany().getTitle()).thenComparing(u -> u.getRole().getDescription()))
                .map(entity -> {
                    UserDto dto = mapperUtil.convert(entity, new UserDto());
                    dto.setIsOnlyAdmin(checkIfOnlyAdminForCompany(dto));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Boolean checkIfOnlyAdminForCompany(UserDto dto) {
        if (dto.getRole().getDescription()==null) return true;
        else if (dto.getRole().getDescription().equalsIgnoreCase("Admin")) {
            List<User> users = userRepository.findAllByCompany_TitleAndRole_Description(dto.getCompany().getTitle(), "Admin");
            return users.size() == 1;
        }
        return false;
    }

    private String getCurrentUserCompanyTitle() {
        String currentUserName = securityService.getLoggedInUser().getUsername();
        return userRepository.findByUsername(currentUserName).getCompany().getTitle();
    }

    private Boolean isCurrentUserRootUser() {

        return securityService.getLoggedInUser().getRole().getDescription().equalsIgnoreCase("root user");
    }


    @Override
    public UserDto save(UserDto userDto) {

        User user = mapperUtil.convert(userDto, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        // userDto.setId(user.getId());
        return mapperUtil.convert(user, userDto);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findUserById(userDto.getId());
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user.getId());
        convertedUser.setPassword(user.getPassword());
        if (securityService.getLoggedInUser().getId().equals(userDto.getId())){
            convertedUser.setRole(user.getRole());
            convertedUser.setUsername(user.getUsername());
        }
        convertedUser.setEnabled(true);
        userRepository.save(convertedUser);
        return findUserById(userDto.getId());
    }





    @Override
    public void delete(Long id) {


        User user = userRepository.findById(id).get();
        if (user==null) {
            throw new NoSuchElementException("User was not found");
        }

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Admin")) {
            user.setIsDeleted(true);
            user.setUsername(user.getUsername() + "-" + user.getId());
            userRepository.save(user);
        }
        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")){
            user.setIsDeleted(true);
            user.setUsername(user.getUsername()+"-" + user.getId());
            userRepository.save(user);
        }

    }


    @Override
    public Boolean emailExist(UserDto userDto) {
        Optional <User> user = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()));
        return user.filter(value -> !value.getId().equals(userDto.getId())).isPresent();
    }

//     @Override
//     public boolean userIsAdmin(UserDto userDto) {
//       // return userRepository.findByUsername(userDto.getUsername()).getRole().getDescription().equalsIgnoreCase("Admin");
//        return securityService.getLoggedInUser().getRole().getDescription().equalsIgnoreCase("Admin");
//
//     }


//      Boolean emailExist(String email) {
//          User user = userRepository.findByEmail(String.valueOf(email));
//         if (user != null) {
//             return true;
//         }
//         return false;
//     }


}