package com.finacial.controller;

import com.finacial.dto.JwtResponse;
import com.finacial.dto.LoginRequestDto;
import com.finacial.dto.MessageResponse;
import com.finacial.dto.SignupRequestDto;
import com.finacial.model.Account;
import com.finacial.model.Role;
import com.finacial.model.Status;
import com.finacial.repository.AccountRepository;
import com.finacial.repository.RoleRepository;
import com.finacial.security.jwt.JwtUtils;
import com.finacial.security.service.TokenVerifyService;
import com.finacial.security.service.UserDetailsImpl;
import com.finacial.service.IAccountService;
import com.finacial.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IAccountService accountService;

    @Autowired
    MailSenderService mailSenderService;

    @Autowired
    Status statusAccNotActived;

    @Autowired
    TokenVerifyService tokenVerifyService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        Optional<Account> accountOpt = accountRepository.findByUsername(loginRequest.getUsername());
        if (!accountOpt.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is not existed!"));
        }
        if (accountOpt.get().getStatus().equals(0L)) {
            return ResponseEntity
                    .badRequest()

                    .body(new MessageResponse("Account is not actived!"));
        }
        String passwordSalt = accountService.getPasswordsaltByUsername(loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), passwordSalt + loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto signUpRequestDto, HttpServletRequest request) {
        if (accountRepository.existsByUsername(signUpRequestDto.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (accountRepository.existsByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        StringBuilder generatePassword = new StringBuilder("");
        String passwordSalt = accountService.generatePassWordSalt();
        generatePassword.append(passwordSalt);
        generatePassword.append(signUpRequestDto.getPassword());
        Account user = new Account(signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                encoder.encode(generatePassword), passwordSalt);

        Set<String> strRoles = signUpRequestDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRole("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByRole("ROLE_MODERATOR")
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByRole("ROLE_USER")
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setStatus(statusAccNotActived.getStatusId());
        Account account = accountRepository.save(user);
        tokenVerifyService.saveTokenSendVerificationToken(account);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
