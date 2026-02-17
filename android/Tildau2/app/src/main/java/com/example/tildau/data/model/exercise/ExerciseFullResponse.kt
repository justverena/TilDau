package com.example.tildau.data.model.exercise

import java.io.Serializable

data class ExerciseFullResponse(
    val id: String,
    val title: String,
    val instruction: String,
    val exerciseType: String, // "READ_ALOUD" или "REPEAT_AFTER_AUDIO"
    val expectedText: String?,
    val referenceAudioUrl: String?
) : Serializable
