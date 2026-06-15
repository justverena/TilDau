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
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val rvCalendar: RecyclerView
    private val tvMonth: TextView
    private val btnPrev: ImageView
    private val btnNext: ImageView

    private var currentMonth: YearMonth = YearMonth.now()

    private var streakDays = setOf<LocalDate>()

    init {

        orientation = VERTICAL

        inflate(context, R.layout.view_calendar, this)

        tvMonth = findViewById(R.id.tvMonth)
        rvCalendar = findViewById(R.id.calendarGrid)

        btnPrev = findViewById(R.id.btnPrevMonth)
        btnNext = findViewById(R.id.btnNextMonth)

        rvCalendar.layoutManager = GridLayoutManager(context, 7)

        btnPrev.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            renderCalendar()
        }

        btnNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            renderCalendar()
        }

        renderCalendar()
    }

    fun setDays(activeDays: Set<LocalDate>) {
        streakDays = activeDays
        renderCalendar()
    }
    private fun renderCalendar() {

        val days = mutableListOf<CalendarDay>()

        val firstDayOfMonth = currentMonth.atDay(1)

        val firstWeekDay = firstDayOfMonth.dayOfWeek.value
        val offset = firstWeekDay - 1

        val daysInMonth = currentMonth.lengthOfMonth()

        repeat(offset) {
            days.add(CalendarDay())
        }

        val today = LocalDate.now()

        for (day in 1..daysInMonth) {

            val date = currentMonth.atDay(day)

            days.add(
                CalendarDay(
                    number = day,
                    isStreak = streakDays.contains(date),
                    isToday = date == today
                )
            )
        }

        while (days.size % 7 != 0) {
            days.add(CalendarDay())
        }

        tvMonth.text =
            "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("kk"))}, ${currentMonth.year}"

        rvCalendar.adapter = CalendarAdapter(days)
    }
}