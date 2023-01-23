package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;

    public RoleServiceImpl(RoleRepository roleRepository, SecurityService securityService, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public RoleDto findRoleById(Long id) {
        return mapperUtil.convert(roleRepository.findRoleById(id),new RoleDto());
    }

    @Override
    public List<RoleDto> getFilteredRolesForCurrentUser() {

        UserDto user = securityService.getLoggedInUser();
        if (user.getRole().getDescription().equals("Root User")) {
            List<RoleDto> list = new ArrayList<>();
            list.add(mapperUtil.convert(roleRepository.findByDescription("Admin"), new RoleDto()));
            return list;
        } else {
            return roleRepository.findAll().stream().filter(role -> !role.getDescription().equals("Root User"))
                    .map(role -> mapperUtil.convert(role, new RoleDto()))
                    .collect(Collectors.toList());
        }

    }
}