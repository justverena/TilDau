package com.example.tildau.ui.course

import com.example.tildau.data.model.course.UnitResponse

sealed class CourseItem {

    data class Header(
        val title: String,
        val description: String
    ) : CourseItem()

    data class Feature(
        val text: String
    ) : CourseItem()

    data class Unit(
        val number: Int,
        val title: String,
        val description: String,
        val unitResponse: UnitResponse
    ) : CourseItem()
}
