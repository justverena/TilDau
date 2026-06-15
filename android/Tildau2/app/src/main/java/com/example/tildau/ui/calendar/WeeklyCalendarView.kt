package com.example.tildau.ui.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class WeeklyCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvMonth: TextView
    private val rvWeek: RecyclerView

    private val btnPrevWeek: ImageView
    private val btnNextWeek: ImageView

    private var streakDays = setOf<LocalDate>()

    // 🔥 CURRENT DISPLAYED WEEK
    private var currentWeekDate = LocalDate.now()

    init {

        orientation = VERTICAL

        inflate(context, R.layout.view_weekly_calendar, this)

        tvMonth = findViewById(R.id.tvWeekMonth)
        rvWeek = findViewById(R.id.rvWeekCalendar)

        btnPrevWeek = findViewById(R.id.btnPrevWeek)
        btnNextWeek = findViewById(R.id.btnNextWeek)

        rvWeek.layoutManager = GridLayoutManager(context, 7)

        // 🔥 PREVIOUS WEEK
        btnPrevWeek.setOnClickListener {

            currentWeekDate = currentWeekDate.minusWeeks(1)

            renderWeek()
        }

        // 🔥 NEXT WEEK
        btnNextWeek.setOnClickListener {

            currentWeekDate = currentWeekDate.plusWeeks(1)

            renderWeek()
        }

        renderWeek()
    }

    fun setDays(activeDays: Set<LocalDate>) {

        streakDays = activeDays

        renderWeek()
    }

    private fun renderWeek() {

        val days = mutableListOf<CalendarDay>()

        val today = LocalDate.now()

        // 🔥 FIND MONDAY OF DISPLAYED WEEK
        val monday = currentWeekDate.minusDays(
            (currentWeekDate.dayOfWeek.value - 1).toLong()
        )

        // 🔥 BUILD 7 DAYS
        for (i in 0..6) {

            val date = monday.plusDays(i.toLong())

            days.add(
                CalendarDay(
                    number = date.dayOfMonth,
                    isToday = date == today,
                    isStreak = streakDays.contains(date)
                )
            )
        }

        tvMonth.text =
            "${monday.month.getDisplayName(TextStyle.FULL, Locale("kk"))} ${monday.year}"

        rvWeek.adapter = CalendarAdapter(days)
    }
}