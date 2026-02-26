package com.example.tildau.data.model.exercise

data class SubmitExerciseResponse(
    val attemptId: String,
    val overallScore: Int,
    val feedback: List<String>
)