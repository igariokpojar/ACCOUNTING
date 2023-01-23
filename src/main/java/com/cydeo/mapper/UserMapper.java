package com.cydeo.mapper;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToEntity(UserDto dto){ // give me the UserDto and I will give you the entity
        return modelMapper.map(dto,User.class);
    }

    public UserDto convertToDto(User entity){ // give me the User entity, and I will give you the UserDto
        return modelMapper.map(entity,UserDto.class);
    }
}
