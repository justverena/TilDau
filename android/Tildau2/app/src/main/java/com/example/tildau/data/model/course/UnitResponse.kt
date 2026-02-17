package com.example.tildau.data.model.course

import java.io.Serializable


data class UnitResponse(
    val id: String,
    val title: String,
    val description: String,
    val exercises: List<ExerciseResponse>
) : Serializable
