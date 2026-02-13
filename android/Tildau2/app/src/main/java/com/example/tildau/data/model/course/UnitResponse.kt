package com.example.tildau.data.model.course

data class UnitResponse(
    val id: String,
    val title: String,
    val description: String,
    val exercises: List<ExerciseResponse>
)
