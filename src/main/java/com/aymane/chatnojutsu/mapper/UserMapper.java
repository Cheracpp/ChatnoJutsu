package com.aymane.chatnojutsu.mapper;

import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;

public class UserMapper {
    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .username(userDTO.username())
                .password(userDTO.password())
                .email(userDTO.email())
                .build();
    }
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                "HIDDEN",
                user.getEmail()
        );
    }
}
