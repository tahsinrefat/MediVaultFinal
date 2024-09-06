package com.medi_vault_final.MediVaultFinal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/v1/templates")
public class TemplateController {

    @GetMapping("/auth/login-page")
    public String loginPage(){
        return "LoginPage";
    }

    @GetMapping("/auth/register-page")
    public String registerPage(){
        return "RegisterPage";
    }
}
