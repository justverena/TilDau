package com.example.tildau.ui.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val rvCalendar: RecyclerView
    private val tvMonth: TextView
    private val tvSubtitle: TextView

    init {
        orientation = VERTICAL
        inflate(context, R.layout.view_calendar, this)

        tvMonth = findViewById(R.id.tvMonth)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        rvCalendar = findViewById(R.id.calendarGrid)

        rvCalendar.layoutManager = GridLayoutManager(context, 7)
    }

    fun setMonthTitle(title: String) {
        tvMonth.text = title
    }

    fun setSubtitle(subtitle: String) {
        tvSubtitle.text = subtitle
    }

    fun setDays(days: List<CalendarDay>) {
        rvCalendar.adapter = CalendarAdapter(days)
    }
}