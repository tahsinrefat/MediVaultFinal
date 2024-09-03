package com.medi_vault_final.MediVaultFinal.dto;

import com.medi_vault_final.MediVaultFinal.enums.Gender;
import com.medi_vault_final.MediVaultFinal.enums.Role;

public record UserDto(Long id,
                      String name,
                      Role role,
                      Integer age,
                      Gender gender,
                      String username,
                      String password) {
}
