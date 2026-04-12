package com.example.tildau.ui.course

import com.example.tildau.data.enums.UnitState
import com.example.tildau.data.model.course.UnitResponse
import com.example.tildau.data.model.course.ExerciseResponse

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
        val unitResponse: UnitResponse,
        val state: UnitState,
        val progress: Int? = null,
        val lastScore: Int? = null,
        var isExpanded: Boolean = false
    ) : CourseItem()

    data class Exercise(
        val exerciseResponse: ExerciseResponse, // или ExerciseResponse
        val parentUnitNumber: Int
    ) : CourseItem()

    object CourseCard : CourseItem()
    data class InfoRow(val sections: Int, val hours: String) : CourseItem()
    data class ProgressBox(val progressPercent: Int, val resumeText: String) : CourseItem()

}
