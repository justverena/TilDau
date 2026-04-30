package com.example.tildau.ui.calendar

import androidx.annotation.DrawableRes
import com.example.tildau.R

data class CalendarDay(
    val number: Int = 0,          // число дня, 0 = пустой
    val isStreak: Boolean = false, // отмечен ли день как streak
    val isToday: Boolean = false,  // текущая дата
    @DrawableRes val streakIcon: Int = R.drawable.ic_streak_on // иконка streak
)