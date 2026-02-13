package com.example.tildau.data.model.course

data class CourseFullResponse(
    val id: String,
    val title: String,
    val description: String,
    val units: List<UnitResponse>
)
