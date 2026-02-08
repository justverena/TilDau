package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.auth.LoginRequest;
import kz.kbtu.tildau.dto.auth.LoginResponse;
import kz.kbtu.tildau.dto.auth.RegisterRequest;
import kz.kbtu.tildau.dto.auth.RegisterResponse;
import kz.kbtu.tildau.dto.user.ApiResponse;
import kz.kbtu.tildau.dto.user.UpdateProfileRequest;
import kz.kbtu.tildau.dto.user.UpdateProfileResponse;
import kz.kbtu.tildau.dto.user.UserResponse;
import kz.kbtu.tildau.exception.UnauthorizedException;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.UserService;
import kz.kbtu.tildau.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userDetails.getUser();
        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getAvatarUrl()
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody UpdateProfileRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userDetails.getUser();
        UpdateProfileResponse response =
                userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteProfile(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        UUID userId = userDetails.getUser().getId();
        userService.deleteProfile(userId);
        return new ApiResponse("Profile deleted successfully");
    }
}