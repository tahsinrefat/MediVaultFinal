package com.medi_vault_final.MediVaultFinal.test_pojo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class PasswordEncoderTest {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String encodePassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }
}
