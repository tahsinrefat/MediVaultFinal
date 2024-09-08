package com.medi_vault_final.MediVaultFinal.exception;

public class InvalidJWTToken extends RuntimeException {
    public InvalidJWTToken(String message) {
        super(message);
    }
}
