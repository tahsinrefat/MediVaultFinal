package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.AuthenticationRequestDto;
import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.dto.UserDto;
import com.medi_vault_final.MediVaultFinal.entity.User;
import com.medi_vault_final.MediVaultFinal.enums.Role;
import com.medi_vault_final.MediVaultFinal.exception.InvalidAuthorityException;
import com.medi_vault_final.MediVaultFinal.exception.InvalidJWTToken;
import com.medi_vault_final.MediVaultFinal.exception.UserNotFoundException;
import com.medi_vault_final.MediVaultFinal.mapper.UserMapper;
import com.medi_vault_final.MediVaultFinal.repository.UserRepository;
import com.medi_vault_final.MediVaultFinal.service.PrescriptionService;
import com.medi_vault_final.MediVaultFinal.service.UserService;
import com.medi_vault_final.MediVaultFinal.service.impl.AuthenticationService;
import com.medi_vault_final.MediVaultFinal.service.impl.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final AuthenticationService authenticationService;

    private final PrescriptionService prescriptionService;

    private final UserRepository userRepository;

    private final JWTService jwtService;

    private final UserService userService;


    @GetMapping("/auth/login-page")
    public String loginPage(Model model){
        model.addAttribute("authenticationRequestDto", new AuthenticationRequestDto(null, null));
        return "LoginPage";
    }

    @PostMapping("/auth/login")
    public String login(@ModelAttribute("authenticationRequestDto") AuthenticationRequestDto authenticationRequestDto, HttpServletResponse httpServletResponse, Model loginModel){
        try {
            ResponseEntity<String> responseEntity = authenticationService.login(authenticationRequestDto);
            String jwtToken = responseEntity.getBody();
            loginModel.addAttribute("isAuthenticated", true);
            if (jwtToken != null){
                Cookie cookie = new Cookie("jwtToken", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(7*24*60*60);
                httpServletResponse.addCookie(cookie);
            }
            return "redirect:/api/v1/templates/auth/home-page/0";
        }catch (Exception e){
            loginModel.addAttribute("isAuthenticated", false);
            loginModel.addAttribute("authenticationRequestDto", new AuthenticationRequestDto(null, null));
            return "redirect:/api/v1/templates/auth/login-page";
        }
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute("userDto") UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/api/v1/templates/auth/login-page";
    }

    @GetMapping("/auth/register-page")
    public String registerPage(@ModelAttribute("userDto") UserDto userDto){
        return "RegisterPage";
    }

    @RequestMapping(value = "/auth/home-page/{page-number}", method = {RequestMethod.GET, RequestMethod.POST})
    public String homePage(Model model, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @PathVariable("page-number") long pageNumber, @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate){
        if (jwtToken.isEmpty()){
            return "redirect:/api/v1/templates/auth/login-page";
        }
        String username = jwtService.extractUsername(jwtToken);
        User user = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));

        model.addAttribute("jwtToken", jwtToken);
        model.addAttribute("username", username);
        model.addAttribute("name", user.getName());
        Pageable pageable = PageRequest.of((int)pageNumber, 10, Sort.by("prescriptionDate").descending());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateFrom = null;
        LocalDate dateTo = null;
        try {
            if (fromDate != null && toDate != null){
                model.addAttribute("fromDate", fromDate);
                model.addAttribute("toDate", toDate);
                dateFrom = LocalDate.parse(fromDate, formatter);
                dateTo = LocalDate.parse(toDate, formatter);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
        }
        Page<PrescriptionDto> prescriptionDto = dateFrom == null && dateTo == null? prescriptionService.getPrescriptionByCurrentMonthAndUser(user.getId(), pageable): prescriptionService.getPrescriptionByDateRangeAndUserId(dateFrom, dateTo, user.getId(), pageable);
        model.addAttribute("prescriptionDto", prescriptionDto);
        model.addAttribute("currentPage", pageNumber);
        long numberOfElements = prescriptionDto.getTotalElements();
        long numberOfButtons = numberOfElements%5;
        model.addAttribute("numberOfButtons", numberOfButtons);
        model.addAttribute("totalPages", prescriptionDto.getTotalPages());
        return "HomePage";
    }

    @GetMapping("/auth/profile")
    public String profile(@RequestParam String jwtToken, @RequestParam String username, Model profileModel){
        User user = new User();
        user.setUsername(username);
        if (jwtService.validateToken(jwtToken, user)){
            User user1 = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username: "+username));
            profileModel.addAttribute("userDto", UserMapper.mapToUserDto(user1));
        }else {
            throw new InvalidJWTToken("JWT token not valid");
        }
        return "ProfilePage";
    }

    @PostMapping("/auth/logout")
    public String logout(HttpServletResponse httpServletResponse, Model loginModel) throws IOException {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
        loginModel.addAttribute("authenticationRequestDto", new AuthenticationRequestDto(null, null));
        return loginPage(loginModel);
    }

    @GetMapping("/auth/user-list/{page-number}")
    public String showAllUserList(@RequestParam String jwtToken, @RequestParam String username, @PathVariable("page-number") long pageNumber, Model allUsersModel){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.ADMIN)){
                Pageable pageable = PageRequest.of((int)pageNumber, 10, Sort.by("id").ascending());
                Page<UserDto> allUsers = userService.getAllUsers(pageable);
                allUsersModel.addAttribute("allUsers", allUsers);
                allUsersModel.addAttribute("currentPage", pageNumber);
                long numberOfElements = allUsers.getTotalElements();
                long numberOfButtons = numberOfElements%5;
                allUsersModel.addAttribute("numberOfButtons", numberOfButtons);
                allUsersModel.addAttribute("totalPages", allUsers.getTotalPages());
                allUsersModel.addAttribute("jwtToken", jwtToken);
                allUsersModel.addAttribute("username", username);
                return "ViewAllUsersPage";
            } else {
                throw new InvalidAuthorityException("You are not authorized to view this page.");
            }
        }
        throw new InvalidJWTToken("Invalid JWT token");
    }

}
