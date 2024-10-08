package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.AuthenticationRequestDto;
import com.medi_vault_final.MediVaultFinal.dto.DateRangeDto;
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
import java.util.List;
import java.util.stream.Collectors;

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
        model.addAttribute("role", user.getRole().name());
        model.addAttribute("gender", user.getGender().name());
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
        Page<Object[]> prescriptionDto = dateFrom == null && dateTo == null? prescriptionService.getPrescriptionByCurrentMonthAndUser(user.getId(), pageable): prescriptionService.getPrescriptionByDateRangeAndUserId(dateFrom, dateTo, user.getId(), pageable);
        model.addAttribute("prescriptionDto", prescriptionDto);
        model.addAttribute("currentPage", pageNumber);
        long numberOfElements = prescriptionDto.getTotalElements();
        long numberOfButtons = numberOfElements%5;
        model.addAttribute("numberOfButtons", numberOfButtons);
        model.addAttribute("totalPages", prescriptionDto.getTotalPages());
        return "HomePage";
    }

    @GetMapping("/auth/profile")
    public String profile(@CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @RequestParam String username, Model profileModel){
        User user = new User();
        user.setUsername(username);
        if (jwtService.validateToken(jwtToken, user)){
            User user1 = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username: "+username));
            profileModel.addAttribute("userDto", UserMapper.mapToUserDto(user1));
            profileModel.addAttribute("profileRole", user1.getRole().name());
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
                List<UserDto> allUsersList = userRepository.findAll().stream().map(UserMapper::mapToUserDto).toList();
                allUsersModel.addAttribute("allUsers", allUsers);
                allUsersModel.addAttribute("currentPage", pageNumber);
                long numberOfElements = allUsers.getTotalElements();
                long numberOfButtons = numberOfElements%5;
                allUsersModel.addAttribute("numberOfButtons", numberOfButtons);
                allUsersModel.addAttribute("totalPages", allUsers.getTotalPages());
                allUsersModel.addAttribute("jwtToken", jwtToken);
                allUsersModel.addAttribute("username", username);
                allUsersModel.addAttribute("totalUsers", allUsersList.size());
                return "ViewAllUsersPage";
            } else {
                throw new InvalidAuthorityException("You are not authorized to view this page.");
            }
        }
        throw new InvalidJWTToken("Invalid JWT token");
    }

    @PostMapping("/auth/create-prescription")
    public String createPrescription(@ModelAttribute("prescriptionDto") PrescriptionDto prescriptionDto, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @RequestParam String username){
        User currentDoctor = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        PrescriptionDto finalPrescriptionDto = new PrescriptionDto(
                null,
                LocalDate.now(),
                prescriptionDto.patientId(),
                currentDoctor.getId(),
                prescriptionDto.nextVisitDate(),
                prescriptionDto.diagnosis(),
                prescriptionDto.medicine()
        );
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+ username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.DOCTOR)){
                prescriptionService.createPrescription(finalPrescriptionDto);
            }else {
                throw new InvalidAuthorityException("You are not authorized for this operation.");
            }
        }else {
            throw new InvalidJWTToken("JWT token is invalid");
        }
        return "redirect:/api/v1/templates/auth/written-prescriptions/0?username=" + username;
    }

    @RequestMapping(value = "/auth/written-prescriptions/{page-number}", method = {RequestMethod.GET, RequestMethod.POST})
    public String writtenPrescriptionPage(@ModelAttribute("dateRangeDto") DateRangeDto dateRangeDto, Model writtenPrescriptionModel, @PathVariable("page-number") long pageNumber, @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @RequestParam String username){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+ username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.DOCTOR)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateFrom = null;
                LocalDate dateTo = null;
                try {
                    if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
                        dateFrom = LocalDate.parse(fromDate, formatter);
                        dateTo = LocalDate.parse(toDate, formatter);
                        writtenPrescriptionModel.addAttribute("fromDate", fromDate);
                        writtenPrescriptionModel.addAttribute("toDate", toDate);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format: " + e.getMessage());
                }
                Pageable pageable = PageRequest.of((int)pageNumber, 10, Sort.by("prescriptionDate").descending());
                Page<Object[]> prescriptionDto = dateFrom == null && dateTo == null? prescriptionService.getPrescriptionByCurrentMonthAndDoctor(currentUser.getId(), pageable): prescriptionService.getPrescriptionByDateRangeAndDoctor(dateFrom, dateTo, currentUser.getId(), pageable);
                writtenPrescriptionModel.addAttribute("name", currentUser.getName());
                writtenPrescriptionModel.addAttribute("prescriptionDto", prescriptionDto);
                writtenPrescriptionModel.addAttribute("username", username);
                writtenPrescriptionModel.addAttribute("currentPage", pageNumber);
                writtenPrescriptionModel.addAttribute("fromDate", fromDate);
                writtenPrescriptionModel.addAttribute("toDate", toDate);
                long numberOfElements = prescriptionDto.getTotalElements();
                long numberOfButtons = numberOfElements%5;
                writtenPrescriptionModel.addAttribute("numberOfButtons", numberOfButtons);
                writtenPrescriptionModel.addAttribute("totalPages", prescriptionDto.getTotalPages());
                return "PrescriptionsWrittenByDoctorPage";
            } else {
                throw new InvalidAuthorityException("You do not have the authority to this operation");
            }
        } else {
            throw new InvalidJWTToken("JWT token is not valid");
        }
    }

    @GetMapping("/auth/create-prescription-page")
    public String createPrescriptionPage(@ModelAttribute("prescriptionDto") PrescriptionDto prescriptionDto, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @RequestParam String username, Model createPrescriptionPageModel){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        if (currentUser.getRole().equals(Role.DOCTOR)){
            if (jwtService.validateToken(jwtToken, currentUser)){
                createPrescriptionPageModel.addAttribute("jwtToken",jwtToken);
                createPrescriptionPageModel.addAttribute("username",username);
                List<User> allExceptDoctor = userRepository.findAll();
                allExceptDoctor.removeIf( user -> user.getUsername().equals(username));
                createPrescriptionPageModel.addAttribute("allUsers",allExceptDoctor.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList()));
                return "CreatePrescriptionPage";
            } else{
                throw new InvalidJWTToken("JWT token is invalid");
            }
        } else {
            throw new InvalidAuthorityException("You are not authorized for this task.");
        }
    }

    @PostMapping("/auth/delete-prescription/{prescription-id}")
    public String deletePrescriptionById(@PathVariable("prescription-id") Long prescriptionId, @RequestParam String username, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            prescriptionService.deletePrescriptionById(prescriptionId);
        }
        return "redirect:/api/v1/templates/auth/home-page/0?username=" + username;
    }

    @PostMapping("/auth/edit-prescription/{prescription-id}")
    public String editPrescriptionById(@PathVariable("prescription-id") Long prescriptionId, @RequestParam String username, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, Model editPrescriptionPageModel, @ModelAttribute("editedPrescription") PrescriptionDto editedPrescriptionDto){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.DOCTOR)){
                PrescriptionDto prescriptionDto = prescriptionService.getPrescriptionById(prescriptionId);
                User patient = userRepository.findById(prescriptionDto.patientId()).orElseThrow(() -> new UserNotFoundException("No user found with ID "+ prescriptionDto.patientId()));
                editPrescriptionPageModel.addAttribute( "prescriptionDto",prescriptionDto);
                editPrescriptionPageModel.addAttribute( "patientName",patient.getName());
                editPrescriptionPageModel.addAttribute("patientAge", patient.getAge());
                editPrescriptionPageModel.addAttribute("patientGender", patient.getGender());
                editPrescriptionPageModel.addAttribute("username", username);
            }
        }
        return "EditPrescriptionPage";
    }

    @PostMapping("/auth/save-edited")
    public String saveEdited(@ModelAttribute("prescriptionDto") PrescriptionDto prescriptionDto, @RequestParam String username, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.DOCTOR)){
                PrescriptionDto prescriptionDto1 = prescriptionService.getPrescriptionById(prescriptionDto.id());
                PrescriptionDto finalPrescriptionDto = new PrescriptionDto(prescriptionDto.id(), prescriptionDto1.prescriptionDate(), prescriptionDto.patientId(), prescriptionDto.doctorId(), prescriptionDto1.nextVisitDate(), prescriptionDto.diagnosis(), prescriptionDto.medicine());
                prescriptionService.updatePrescriptionById(prescriptionDto.id(), finalPrescriptionDto);
                return "redirect:/api/v1/templates/auth/written-prescriptions/0?username=" + username;
            } else{
                throw new InvalidAuthorityException("You do not have authorization to this operation");
            }
        } else {
            throw new InvalidJWTToken("JWT token is not valid");
        }
    }

    @RequestMapping(value = "/auth/prescription-report/{page-number}", method = {RequestMethod.GET, RequestMethod.POST})
    public String prescriptionReport(@PathVariable("page-number") long pageNumber, Model prescriptionReportModel, @RequestParam(required = false) String dateLocal, @CookieValue(value = "jwtToken", defaultValue = "") String jwtToken, @RequestParam String username){
        User currentUser = userRepository.findByUsername(username).orElseThrow( () -> new UserNotFoundException("No user found with username "+ username));
        if (jwtService.validateToken(jwtToken, currentUser)){
            if (currentUser.getRole().equals(Role.ADMIN)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = null;
                System.out.println(dateLocal);
                try {
                    if (dateLocal != null && !dateLocal.isEmpty()) {
                        localDate = LocalDate.parse(dateLocal, formatter);

                    }else {
                        localDate = LocalDate.now();
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format: " + e.getMessage());
                }
                Pageable pageable = PageRequest.of((int)pageNumber, 10, Sort.by("id").descending());

                Page<Object[]> prescriptionCountByDate = prescriptionService.getPrescriptionCountByDate(localDate, pageable);
                prescriptionReportModel.addAttribute("prescriptionCountByDate", prescriptionCountByDate);
                prescriptionReportModel.addAttribute("username", username);
                prescriptionReportModel.addAttribute("currentPage", pageNumber);
                long numberOfElements = prescriptionCountByDate.getTotalElements();
                long numberOfButtons = numberOfElements%5;
                prescriptionReportModel.addAttribute("numberOfButtons", numberOfButtons);
                prescriptionReportModel.addAttribute("totalPages", prescriptionCountByDate.getTotalPages());
                return "PrescriptionReportPage";
            } else {
                throw new InvalidAuthorityException("You don't have the authority to this operation");
            }
        } else {
            throw new InvalidJWTToken("JWT token is not valid");
        }
    }
}
