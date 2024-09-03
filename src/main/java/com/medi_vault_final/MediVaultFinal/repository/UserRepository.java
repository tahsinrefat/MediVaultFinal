package com.medi_vault_final.MediVaultFinal.repository;

import com.medi_vault_final.MediVaultFinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
