package com.example.tildau.data.model.course

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExerciseResponse(
    val id: String,
    val title: String,
    val instruction: String,
    @SerializedName("completed")
    val isCompleted: Boolean,

    @SerializedName("locked")
    val isLocked: Boolean
) : Serializable
