package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.AuthenticationRequestDto;
import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import com.medi_vault_final.MediVaultFinal.service.UserService;
import com.medi_vault_final.MediVaultFinal.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class SecurityController {
    private final AuthenticationService authenticationService;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticationRequestDto requestDto){
        return authenticationService.login(requestDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);
        return ResponseEntity.ok(savedUser);
    }
}
