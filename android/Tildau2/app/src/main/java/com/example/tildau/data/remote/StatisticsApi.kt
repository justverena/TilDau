package com.example.tildau.data.remote

import com.example.tildau.data.model.stats.AchievementResponse
import com.example.tildau.data.model.stats.ActivityDayDto
import com.example.tildau.data.model.stats.SkillTrendDto
import retrofit2.http.GET

interface StatisticsApi {

    @GET("api/user/stats/current-streak")
    suspend fun getStreak(): Int

    @GET("api/user/stats/skill-trend")
    suspend fun getSkillTrend(): SkillTrendDto

    @GET("api/user/stats/activity-calendar")
    suspend fun getActivityCalendar(): List<ActivityDayDto>

    @GET("api/user/stats/achievements")
    suspend fun getAchievements(): List<AchievementResponse>


}