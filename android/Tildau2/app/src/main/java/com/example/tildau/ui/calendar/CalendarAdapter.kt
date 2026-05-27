package com.example.tildau.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R

class CalendarAdapter(
    private val days: List<CalendarDay>
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.tvNumber)
        val streak: ImageView = view.findViewById(R.id.ivStreak)

        val bgCircle: View = view.findViewById(R.id.bgCircle)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]

        if (day.number == 0) {
            // ❗ полностью скрываем ячейку
            holder.itemView.visibility = View.INVISIBLE
        } else {
            holder.itemView.visibility = View.VISIBLE

            holder.number.text = day.number.toString()

            if (day.isToday) {

                holder.bgCircle.setBackgroundResource(
                    R.drawable.bg_today_circle
                )

                holder.number.setTextColor(
                    holder.itemView.context.getColor(R.color.white)
                )

            } else {

                holder.bgCircle.setBackgroundResource(
                    R.drawable.bg_day_circle
                )

                holder.number.setTextColor(
                    holder.itemView.context.getColor(R.color.white)
                )
            }

            holder.streak.visibility =
                if (day.isStreak) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int = days.size
}