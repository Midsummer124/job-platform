package com.example.authserver.service;

import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.model.CompanyUser;
import com.example.authserver.model.User;
import com.example.authserver.model.UserType;
import com.example.authserver.repository.CompanyUserRepository;
import com.example.authserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyUserRepository companyUserRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyUserRepository companyUserRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyUserRepository = companyUserRepository;
    }

    // *** Method to register a regular USER ***
    @Transactional
    public User registerRegularUser(String username, String password, String email, String phone) throws UserAlreadyExistsException {
        // Call the internal save method with UserType.USER
        return registerUser(username, password, email, phone, UserType.USER);
    }

    // *** Method to register an ENTERPRISE user ***
    @Transactional
    public User registerEnterpriseUser(String username, String password, String email, String phone, Long company_id, String position, String department) throws UserAlreadyExistsException {
        // Call the internal save method with UserType.ENTERPRISE
        User savedUser = registerUser(username, password, email, phone, UserType.ENTERPRISE);
        CompanyUser companyUser = new CompanyUser(
                savedUser.getId(),
                company_id,
                position,
                department
        );
        companyUserRepository.save(companyUser);
        return savedUser;
    }

    // *** Internal method to handle the actual user creation and saving ***
    // Keep transactional here as it's the core save logic
    private User registerUser(String username, String password, String email, String phone, UserType userType) throws UserAlreadyExistsException {
        // Check for existing user by username OR email (based on unique constraints)
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("User with username " + username + " already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        // Create the new User entity
        User newUser = new User(
                username,
                passwordEncoder.encode(password), // Hash the password
                email,
                phone,
                userType // *** Use the passed userType ***
        );

        // Save the user to the database
        User savedUser = userRepository.save(newUser);
        System.out.println("User saved to database: " + savedUser.getUsername() + " with type: " + savedUser.getUserType());
        return savedUser;
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
