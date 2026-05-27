package com.example.tildau.data.model.exercise

import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.stats.AchievementResponse

data class SubmitExerciseResponse(
    val attemptId: String?,
    val overallScore: Int,
    val feedback: List<String>,
    val nextStep: NextStepResponse,
    val newAchievements: List<AchievementResponse> = emptyList()
)