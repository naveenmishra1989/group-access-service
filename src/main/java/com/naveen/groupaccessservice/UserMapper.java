package com.naveen.groupaccessservice;

import com.naveen.groupaccessservice.dto.UserDTO;
import com.naveen.groupaccessservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDto(User user);
    User userDtoToUser(UserDTO userDto);
}

