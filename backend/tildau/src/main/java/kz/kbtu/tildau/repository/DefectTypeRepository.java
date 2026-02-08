package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.DefectType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefectTypeRepository extends JpaRepository<DefectType, Integer> {
}