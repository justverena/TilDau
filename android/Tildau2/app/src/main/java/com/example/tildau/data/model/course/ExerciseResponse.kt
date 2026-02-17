package com.example.tildau.data.model.course

import java.io.Serializable

data class ExerciseResponse(
    val id: String,
    val title: String,
    val instruction: String
) : Serializable
