package com.example.authserver.controller;

import com.example.authserver.dto.LoginRequest;
import com.example.authserver.dto.LoginResponse;
import com.example.authserver.dto.RegisterRequest; // Use the same RegisterRequest DTO
import com.example.authserver.dto.UserDetailsDto;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.service.AuthService;
import com.example.authserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // Remove the old /auth/register method if you no longer need it for a generic registration
    // @PostMapping("/register")
    // public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) { ... }


    // *** New endpoint for Regular User Registration ***
    @PostMapping("/register/user")
    public ResponseEntity<?> registerRegularUser(@RequestBody RegisterRequest registerRequest) {
        try {
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null) {
                return ResponseEntity.badRequest().body("Username, password, and email are required.");
            }
            // Phone is optional

            // Call the specific UserService method for regular users
            userService.registerRegularUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getPhone()
            );
            return ResponseEntity.ok("Regular user registered successfully!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Regular user registration failed: " + e.getMessage());
        }
    }

    // *** New endpoint for Enterprise User Registration ***
    @PostMapping("/register/enterprise")
    public ResponseEntity<?> registerEnterpriseUser(@RequestBody RegisterRequest registerRequest) {
        try {
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null || registerRequest.getCompany_id() == null) {
                return ResponseEntity.badRequest().body("username, password, email and company_id are required.");
            }
            // Phone is optional

            // Call the specific UserService method for enterprise users
            userService.registerEnterpriseUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getPhone(),
                    registerRequest.getCompany_id(),
                    registerRequest.getPosition(),
                    registerRequest.getDepartment()
            );
            return ResponseEntity.ok("Enterprise user registered successfully!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Enterprise user registration failed: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }

            String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
        UserDetailsDto userDetails = authService.validateTokenAndGetUserDetails(token);
        if (userDetails != null) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }
}
