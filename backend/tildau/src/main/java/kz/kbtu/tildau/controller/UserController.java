package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.auth.LoginRequest;
import kz.kbtu.tildau.dto.auth.LoginResponse;
import kz.kbtu.tildau.dto.auth.RegisterRequest;
import kz.kbtu.tildau.dto.auth.RegisterResponse;
import kz.kbtu.tildau.dto.user.*;
import kz.kbtu.tildau.entity.DefectType;
import kz.kbtu.tildau.exception.UnauthorizedException;
import kz.kbtu.tildau.repository.DefectTypeRepository;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.UserDefectTypeService;
import kz.kbtu.tildau.service.UserService;
import kz.kbtu.tildau.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserDefectTypeService userDefectTypeService;
    private final DefectTypeRepository defectTypeRepository;

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
        User user = userDetails.getUser();
        UpdateProfileResponse response =
                userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteProfile(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        userService.deleteProfile(userId);
        return new ApiResponse("Profile deleted successfully");
    }

    @PostMapping("/me/defects")
    public ResponseEntity<?> setDefects(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody SetUserDefectsRequest request
    ) {
        userDefectTypeService.setUserDefects(
                userDetails.getUser().getId(),
                request.getDefectTypeId()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/defects/status")
    public ResponseEntity<Map<String, Boolean>> hasDefects(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        boolean hasDefects = userDefectTypeService.hasUserDefects(
                userDetails.getUser().getId()
        );

        return ResponseEntity.ok(Map.of("hasDefects", hasDefects));
    }

    @GetMapping("/defect-types")
    public ResponseEntity<List<DefectType>> getDefectTypes() {
        return ResponseEntity.ok(defectTypeRepository.findAll());
    }
}