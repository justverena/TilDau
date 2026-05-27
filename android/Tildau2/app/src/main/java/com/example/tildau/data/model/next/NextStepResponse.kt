package com.example.tildau.data.model.next

import java.io.Serializable

data class NextStepResponse(
    val type: NextStepType,
    val id: String? // UUID как String
) : Serializable