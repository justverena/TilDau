package com.example.tildau.data.model.exercise

import java.io.Serializable

data class ExerciseFullResponse(
    val id: String,
    val title: String,
    val instruction: String,
    val exerciseType: ExerciseType,
    val expectedText: String?,
    val referenceAudioUrl: String?
) : Serializable