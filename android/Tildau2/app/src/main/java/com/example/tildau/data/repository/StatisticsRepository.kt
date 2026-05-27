package com.example.tildau.data.repository

import com.example.tildau.data.remote.StatisticsApi

class StatisticsRepository(
    private val api: StatisticsApi
) {

    suspend fun getStreak() = api.getStreak()

    suspend fun getSkillTrend() = api.getSkillTrend()

    suspend fun getCalendar() = api.getActivityCalendar()
}