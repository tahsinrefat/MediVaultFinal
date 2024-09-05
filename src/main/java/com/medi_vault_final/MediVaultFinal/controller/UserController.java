package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import com.medi_vault_final.MediVaultFinal.entity.User;
import com.medi_vault_final.MediVaultFinal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> getUserById(@RequestBody UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);
        return ResponseEntity.ok(savedUser);
    }

    //viewing profile for all roles
    @GetMapping("/any/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId, Principal principal){
        UserDto userDto = userService.getUserById(userId);
        if (principal.getName().equals(userDto.username())){
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.ok(null);
    }

    //viewing all user lists for admin
    @GetMapping("/admin/get-all-user-list")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable){
        Page<UserDto> allUsers = userService.getAllUsers(pageable);
        return ResponseEntity.ok(allUsers);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable("id") Long userId, @RequestBody UserDto updatedUserDto){
        UserDto updatedUser = userService.updateUser(userId, updatedUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok("User has been deleted successfully.");
    }
}
