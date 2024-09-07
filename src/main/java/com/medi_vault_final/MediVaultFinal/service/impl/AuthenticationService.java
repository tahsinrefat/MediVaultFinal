package com.medi_vault_final.MediVaultFinal.service.impl;

import com.medi_vault_final.MediVaultFinal.dto.AuthenticationRequestDto;
import com.medi_vault_final.MediVaultFinal.entity.User;
import com.medi_vault_final.MediVaultFinal.exception.BadCredentialsException;
import com.medi_vault_final.MediVaultFinal.exception.UserNotFoundException;
import com.medi_vault_final.MediVaultFinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final AuthenticationProvider authenticationProvider;

    private final JWTService jwtService;

    public ResponseEntity<String> login(AuthenticationRequestDto authenticationRequestDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDto.username(),
                        authenticationRequestDto.password()
                )
        );

        User user = userRepository.findByUsername(authenticationRequestDto.username()).orElseThrow( () -> new UserNotFoundException("No user found with username "+authenticationRequestDto.username()));

        if (authentication.isAuthenticated()){
            return jwtService.generateToken(user);
        }
//        return ResponseEntity.badRequest().body("Bad credentials");
        throw new BadCredentialsException("You have entered wrong email or password.");
    }
}
