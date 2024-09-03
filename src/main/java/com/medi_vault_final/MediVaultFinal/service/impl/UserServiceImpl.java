package com.medi_vault_final.MediVaultFinal.service.impl;

import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import com.medi_vault_final.MediVaultFinal.entity.User;
import com.medi_vault_final.MediVaultFinal.exception.UserNotFoundException;
import com.medi_vault_final.MediVaultFinal.mapper.UserMapper;
import com.medi_vault_final.MediVaultFinal.repository.UserRepository;
import com.medi_vault_final.MediVaultFinal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow( () -> new UserNotFoundException("User not found with ID "+userId));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> allUsers = userRepository.findAll(pageable);
        return allUsers.map(UserMapper::mapToUserDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUserDto) {
        User user = userRepository.findById(userId).orElseThrow( () -> new UserNotFoundException("User not found with ID "+userId));
        user.setName(updatedUserDto.name());
        user.setRole(updatedUserDto.role());
        user.setAge(updatedUserDto.age());
        user.setGender(updatedUserDto.gender());
        user.setUsername(updatedUserDto.username());
        user.setPassword(updatedUserDto.password());

        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow( () -> new UserNotFoundException("User doesn't exist with ID "+userId));
        userRepository.deleteById(userId);
    }
}
