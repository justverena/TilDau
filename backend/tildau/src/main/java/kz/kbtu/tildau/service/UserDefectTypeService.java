package kz.kbtu.tildau.service;

import kz.kbtu.tildau.embeddedId.UserDefectTypeId;
import kz.kbtu.tildau.entity.DefectType;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.entity.UserDefectType;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.DefectTypeRepository;
import kz.kbtu.tildau.repository.UserDefectTypeRepository;
import kz.kbtu.tildau.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDefectTypeService {

    private final UserJpaRepository userJpaRepository;
    private final DefectTypeRepository defectTypeRepository;
    private final UserDefectTypeRepository userDefectTypeRepository;

    public void setUserDefects(UUID userId, Integer defectTypeId) {
        User user = getUserOrThrow(userId);
        checkRole(user);

        DefectType defect = getDefectTypeOrThrow(defectTypeId);

        userDefectTypeRepository.deleteByUserId(userId);
        UserDefectType userDefectType = buildUserDefectType(user, defect);
        userDefectTypeRepository.save(userDefectType);
    }

    public boolean hasUserDefects(UUID userId) {
        return userDefectTypeRepository.existsByUserId(userId);
    }

    public DefectType getUserDefectOrThrow(UUID userId) {
        return userDefectTypeRepository.findByUserId(userId)
                .map(UserDefectType::getDefectType)
                .orElseThrow(() -> new ForbiddenException("User must select defect type first"));
    }

    private UserDefectType buildUserDefectType(User user, DefectType defect) {
        return UserDefectType.builder()
                .id(new UserDefectTypeId(user.getId(), defect.getId()))
                .user(user)
                .defectType(defect)
                .build();
    }

    private DefectType getDefectTypeOrThrow(Integer defectId) {
        return defectTypeRepository.findById(defectId)
                .orElseThrow(() -> new RuntimeException("Defect type not found"));
    }

    private void checkRole(User user) {
        if (!user.getRole().getName().equals("user")) {
            throw new ForbiddenException("Only users can have defect type");
        }
    }

    private User getUserOrThrow(UUID userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}