package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.*;
import kz.kbtu.tildau.entity.Role;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.exception.BadRequestException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.exception.UnauthorizedException;
import kz.kbtu.tildau.repository.RoleRepository;
import kz.kbtu.tildau.repository.UserJpaRepository;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserJpaRepository userJpaRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userJpaRepository = userJpaRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()
                || request.getEmail() == null || request.getEmail().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }

        if (!request.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BadRequestException("Invalid email format");
        }

        Optional<User> existingUser = userJpaRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }

        Role defaultRole = roleRepository.findByName("user")
                .orElseThrow(() -> new NotFoundException("Default role not found"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(defaultRole);

        userJpaRepository.save(user);
        return new RegisterResponse("User successfully registered");
    }

    public LoginResponse login(LoginRequest request) {
        User user = userJpaRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }
        UserDetails userDetails = new CustomerUserDetails(user);
        String token = jwtTokenProvider.generateToken(userDetails);
        return new LoginResponse(token);
    }

    public UpdateProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getEmail() != null) {
            if (!isValidEmail(request.getEmail())) {
                throw new BadRequestException("Invalid email format");
            }
            if (!request.getEmail().equals(user.getEmail())) {
                userJpaRepository.findByEmail(request.getEmail())
                        .ifPresent(existingUser -> {
                            throw new BadRequestException("Email already in use");
                        });

                user.setEmail(request.getEmail());
            }
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }

        userJpaRepository.save(user);

        return new UpdateProfileResponse("Profile updated successfully");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public void deleteProfile(UUID userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userJpaRepository.delete(user);
    }
}