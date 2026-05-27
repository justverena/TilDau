package com.example.tildau.data.provider

import com.example.tildau.data.model.stats.ActivityDayDto
import com.example.tildau.data.model.stats.SkillTrendDto
import com.example.tildau.data.remote.StatisticsApi
import com.example.tildau.data.remote.ExerciseApi
import com.example.tildau.data.model.exercise.SubmitExerciseResponse
import okhttp3.MultipartBody

class StatisticsDataProvider(
    private val statisticsApi: StatisticsApi,
    private val exerciseApi: ExerciseApi
) {

    suspend fun getStreak(): Int {
        return statisticsApi.getStreak()
    }

    suspend fun getSkillTrend(): SkillTrendDto {
        return statisticsApi.getSkillTrend()
    }

    suspend fun getCalendar(): List<ActivityDayDto> {
        return statisticsApi.getActivityCalendar()
    }

    // ✅ ПРАВИЛЬНО: без userId
    suspend fun submitExercise(
        exerciseId: String,
        file: MultipartBody.Part
    ): SubmitExerciseResponse {
        return exerciseApi.submitExercise(exerciseId, file)
    }
}