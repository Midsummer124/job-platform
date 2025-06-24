package com.example.authserver.controller;

import com.example.authserver.dto.LoginRequest;
import com.example.authserver.dto.LoginResponse;
import com.example.authserver.dto.RegisterRequest;
import com.example.authserver.dto.UserDetailsDto;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.service.AuthService;
import com.example.authserver.service.UserService;
import com.example.authserver.utils.LogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    private LogProducer logProducer;

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
            logProducer.sendLog("auth-server", "INFO", "正在注册普通用户");
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null) {
                logProducer.sendLog("auth-server", "INFO", "注册无效，缺失必要信息");
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
            logProducer.sendLog("auth-server", "INFO", "成功注册");
            return ResponseEntity.ok("Regular user registered successfully!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logProducer.sendLog("auth-server", "INFO", "注册失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Regular user registration failed: " + e.getMessage());
        }
    }

    // *** New endpoint for Enterprise User Registration ***
    @PostMapping("/register/enterprise")
    public ResponseEntity<?> registerEnterpriseUser(@RequestBody RegisterRequest registerRequest) {
        try {
            logProducer.sendLog("auth-server", "INFO", "正在注册公司用户");
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null || registerRequest.getCompany_id() == null) {
                logProducer.sendLog("auth-server", "INFO", "注册无效，缺失必要信息");
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
            logProducer.sendLog("auth-server", "INFO", "成功注册公司用户");
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
                logProducer.sendLog("auth-server", "INFO", "登录无效，缺失必要信息");
                return ResponseEntity.badRequest().body("Username and password are required.");
            }
            logProducer.sendLog("auth-server", "INFO", "登录成功，发放token");
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
