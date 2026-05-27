package com.example.tildau.data.model.stats

import java.io.Serializable

data class AchievementResponse(
    val code: String,
    val title: String,
    val description: String
) : Serializable