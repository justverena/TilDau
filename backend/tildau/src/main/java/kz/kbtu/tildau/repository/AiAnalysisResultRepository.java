package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.AiAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface AiAnalysisResultRepository extends JpaRepository<AiAnalysisResult, UUID> {
}
