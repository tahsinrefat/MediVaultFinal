package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.AuthenticationRequestDto;
import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.entity.User;
import com.medi_vault_final.MediVaultFinal.exception.UserNotFoundException;
import com.medi_vault_final.MediVaultFinal.repository.UserRepository;
import com.medi_vault_final.MediVaultFinal.service.PrescriptionService;
import com.medi_vault_final.MediVaultFinal.service.impl.AuthenticationService;
import com.medi_vault_final.MediVaultFinal.service.impl.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final AuthenticationService authenticationService;

    private final PrescriptionService prescriptionService;

    private final UserRepository userRepository;

    private final JWTService jwtService;

    @GetMapping("/auth/login-page")
    public String loginPage(Model model){
        model.addAttribute("authenticationRequestDto", new AuthenticationRequestDto(null, null));
        return "LoginPage";
    }

    @PostMapping("/auth/login")
    public String login(@ModelAttribute("authenticationRequestDto") AuthenticationRequestDto authenticationRequestDto, HttpServletResponse httpServletResponse){
        ResponseEntity<String> responseEntity = authenticationService.login(authenticationRequestDto);
        String jwtToken = responseEntity.getBody();
        if (jwtToken != null){
            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(7*24*60*60);
            httpServletResponse.addCookie(cookie);
        }
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", "/api/v1/templates/auth/home-page")
//                .build();
        return "redirect:/api/v1/templates/auth/home-page/0";
    }

    @GetMapping("/auth/register-page")
    public String registerPage(){
        return "RegisterPage";
    }

    @GetMapping("/auth/home-page/{page-number}")
    public String homePage(Model model, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @PathVariable("page-number") long pageNumber){
        if (jwtToken.isEmpty()){
            return "redirect:/api/v1/templates/auth/login-page";
        }
        String username = jwtService.extractUsername(jwtToken);
        User user = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));

        model.addAttribute("username", username);
        model.addAttribute("name", user.getName());
        Pageable pageable = PageRequest.of((int)pageNumber, 10);
        Page<PrescriptionDto> prescriptionDto = prescriptionService.getPrescriptionByCurrentMonthAndUser(user.getId(), pageable);
        model.addAttribute("prescriptionDto", prescriptionDto);
        model.addAttribute("currentPage", pageNumber);
        long numberOfElements = prescriptionDto.getTotalElements();
        long numberOfButtons = numberOfElements%5;
        model.addAttribute("numberOfButtons", numberOfButtons);
        model.addAttribute("totalPages", prescriptionDto.getTotalPages());
        return "HomePage";
    }
}
