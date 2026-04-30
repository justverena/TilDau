package kz.kbtu.tildau.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StatsControllerIntegrationTest extends BaseIntegrationTest {

    protected UUID exerciseId;
    protected String token;

    @BeforeEach
    void setupStatsData() throws Exception {

        token = loginAndGetToken();
        exerciseId = insertExercise(unitId, 1);
        insertStreakAndActivity(userId, exerciseId);
        insertSkillTrend();
    }

    @Test
    void getCurrentStreak_success() throws Exception {

        mockMvc.perform(get("/api/user/stats/current-streak")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void getActivityCalendar_success() throws Exception {

        mockMvc.perform(get("/api/user/stats/activity-calendar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getSkillTrend_success() throws Exception {

        mockMvc.perform(get("/api/user/stats/skill-trend")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overall").value(90.0))
                .andExpect(jsonPath("$.fluency").value(90.0))
                .andExpect(jsonPath("$.pronunciation").value(90.0));
    }

    @Test
    void getAchievements_success() throws Exception {

        mockMvc.perform(get("/api/user/stats/achievements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void stats_endpoints_fail_without_token() throws Exception {

        mockMvc.perform(get("/api/user/stats/current-streak")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/user/stats/activity-calendar")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/user/stats/skill-trend")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/user/stats/achievements")).andExpect(status().isForbidden());
    }
}