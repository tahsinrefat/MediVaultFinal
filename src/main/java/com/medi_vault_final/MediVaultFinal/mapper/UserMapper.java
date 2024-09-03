package com.medi_vault_final.MediVaultFinal.mapper;

import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import com.medi_vault_final.MediVaultFinal.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getAge(),
                user.getGender(),
                user.getUsername(),
                user.getPassword()
        );
    }

    public static User mapToUser(UserDto userDto){
        return User.builder()
                .id(userDto.id())
                .name(userDto.name())
                .role(userDto.role())
                .age(userDto.age())
                .gender(userDto.gender())
                .username(userDto.username())
                .password(userDto.password())
                .build();
    }
}
