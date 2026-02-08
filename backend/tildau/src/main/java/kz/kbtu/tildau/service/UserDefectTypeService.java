package kz.kbtu.tildau.service;

import kz.kbtu.tildau.entity.DefectType;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.entity.UserDefectType;
import kz.kbtu.tildau.repository.DefectTypeRepository;
import kz.kbtu.tildau.repository.UserDefectTypeRepository;
import kz.kbtu.tildau.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDefectTypeService {

    private final UserJpaRepository userJpaRepository;
    private final DefectTypeRepository defectTypeRepository;
    private final UserDefectTypeRepository userDefectTypeRepository;

    public void assignDefectType(UUID userId, Integer defectTypeId) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().getName().equals("user")) {
            throw new RuntimeException("Only users can have defect type");
        }

        DefectType defectType = defectTypeRepository.findById(defectTypeId)
                .orElseThrow(() -> new RuntimeException("Defect type not found"));

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(defectType);

        userDefectTypeRepository.save(userDefectType);
    }
}