package com.medi_vault_final.MediVaultFinal.service;

import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto updateUser(Long userId, UserDto updatedUserDto);

    void deleteUser(Long userId);
}
