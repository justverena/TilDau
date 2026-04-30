package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.stats.AchievementResponse;
import kz.kbtu.tildau.dto.stats.ActivityDayResponse;
import kz.kbtu.tildau.dto.stats.SkillTrendResponse;
import kz.kbtu.tildau.dto.stats.StatsResponse;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.AchievementService;
import kz.kbtu.tildau.service.StatsService;
import kz.kbtu.tildau.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kz.kbtu.tildau.dto.user.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final AchievementService achievementService;

    @GetMapping("/current-streak")
    public ResponseEntity<Integer> getCurrentStreak(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        return ResponseEntity.ok(statsService.getUserCurrentStreak(userId));
    }

    @GetMapping("/activity-calendar")
    public ResponseEntity<List<ActivityDayResponse>>getActivityCalendar(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        return ResponseEntity.ok(statsService.getUserActivityCalendar(userId));
    }

    @GetMapping("/skill-trend")
    public ResponseEntity<SkillTrendResponse>getSkillTrend(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        return ResponseEntity.ok(statsService.getUserSkillTrend(userId));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementResponse>> getAchievements(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

}