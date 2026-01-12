package kz.kbtu.tildau.service;
import kz.kbtu.tildau.dto.LoginRequest;
import kz.kbtu.tildau.dto.LoginResponse;
import kz.kbtu.tildau.dto.RegisterRequest;
import kz.kbtu.tildau.dto.RegisterResponse;
import kz.kbtu.tildau.entity.Role;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.repository.RoleRepository;
import kz.kbtu.tildau.repository.UserJpaRepository;
import kz.kbtu.tildau.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setName("John");
        validRegisterRequest.setEmail("john@example.com");
        validRegisterRequest.setPassword("password123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("john@example.com");
        validLoginRequest.setPassword("password123");

        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("mocked-jwt-token");
    }

    @Test
    void register_Success() {
        Role roleUser = new Role();
        roleUser.setId(2);
        roleUser.setName("user");

        when(userJpaRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(roleRepository.findByName("user"))
                .thenReturn(Optional.of(roleUser));

        RegisterResponse response = userService.register(validRegisterRequest);

        assertEquals("User successfully registered", response.getMessage());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userJpaRepository, times(1)).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("John", savedUser.getName());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(2, savedUser.getRole().getId());
    }

    @Test
    void register_ThrowsException_WhenFieldsMissing() {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("email@example.com");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(invalidRequest));

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void register_ThrowsException_WhenEmailInvalid() {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName("John");
        invalidRequest.setEmail("not-an-email");
        invalidRequest.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(invalidRequest));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void login_Success() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail(validLoginRequest.getEmail());
        mockUser.setPassword("encodedPassword");

        when(userJpaRepository.findByEmail(validLoginRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), "encodedPassword"))
                .thenReturn(true);

        LoginResponse response = userService.login(validLoginRequest);

        assertNotNull(response.getToken());
    }

    @Test
    void login_Fails_WhenEmailNotFound() {
        when(userJpaRepository.findByEmail(validLoginRequest.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(validLoginRequest));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_Fails_WhenPasswordWrong() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail(validLoginRequest.getEmail());
        mockUser.setPassword("encodedPassword");

        when(userJpaRepository.findByEmail(validLoginRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), "encodedPassword"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(validLoginRequest));

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void register_ThrowsException_WhenEmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setEmail(validRegisterRequest.getEmail());
        when(userJpaRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(validRegisterRequest));

        assertEquals("User with this email already exists", exception.getMessage());
    }
}