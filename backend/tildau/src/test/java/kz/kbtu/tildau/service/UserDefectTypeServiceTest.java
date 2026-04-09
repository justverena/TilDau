package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.course.*;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDefectTypeServiceTest {

    @Mock private UserJpaRepository userJpaRepository;
    @Mock private DefectTypeRepository defectTypeRepository;
    @Mock private UserDefectTypeRepository userDefectTypeRepository;

    @InjectMocks private UserDefectTypeService userDefectTypeService;

    private UUID userId;
    private DefectType articulationDefect;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articulationDefect = new DefectType();
        articulationDefect.setId(1);
        articulationDefect.setName("articulation");
    }

    @Test
    void getUserDefectOrThrow_ReturnsDefect() {
        User user = new User();
        user.setId(userId);
        UserDefectType userDefectType = UserDefectType.builder().user(user).defectType(articulationDefect).build();

        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));

        DefectType result = userDefectTypeService.getUserDefectOrThrow(userId);
        assertEquals(articulationDefect, result);
    }

    @Test
    void getUserDefectOrThrow_ThrowsIfNoDefect() {
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> userDefectTypeService.getUserDefectOrThrow(userId));

        assertEquals("User must select defect type first", ex.getMessage());
    }

    @Test
    void setUserDefects_Success() {
        User user = new User();
        user.setId(userId);
        user.setRole(new Role("user"));
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(defectTypeRepository.findById(1)).thenReturn(Optional.of(articulationDefect));
        when(userDefectTypeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        userDefectTypeService.setUserDefects(userId, 1);

        verify(userDefectTypeRepository).deleteByUserId(userId);
        verify(userDefectTypeRepository).save(any(UserDefectType.class));
    }

    @Test
    void setUserDefects_ThrowsIfUserNotFound() {
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userDefectTypeService.setUserDefects(userId, 1));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void setUserDefects_ThrowsIfRoleInvalid() {
        User user = new User();
        user.setId(userId);
        user.setRole(new Role("admin"));
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> userDefectTypeService.setUserDefects(userId, 1));

        assertEquals("Only users can have defect type", ex.getMessage());
    }

    @Test
    void setUserDefects_ThrowsIfDefectNotFound() {
        User user = new User();
        user.setId(userId);
        user.setRole(new Role("user"));
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(defectTypeRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userDefectTypeService.setUserDefects(userId, 1));

        assertEquals("Defect type not found", ex.getMessage());
    }

    @Test
    void hasUserDefects_ReturnsTrueOrFalse() {
        when(userDefectTypeRepository.existsByUserId(userId)).thenReturn(true);
        assertTrue(userDefectTypeService.hasUserDefects(userId));

        when(userDefectTypeRepository.existsByUserId(userId)).thenReturn(false);
        assertFalse(userDefectTypeService.hasUserDefects(userId));
    }
}