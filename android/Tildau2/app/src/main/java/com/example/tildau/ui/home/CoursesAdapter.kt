package com.example.tildau.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R

class CoursesAdapter(private val courses: List<Course>) :
    RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tvCourseName)
        val type = itemView.findViewById<TextView>(R.id.tvCourseType)
        val details = itemView.findViewById<TextView>(R.id.tvCourseDetails)
        val duration = itemView.findViewById<TextView>(R.id.tvCourseDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.name.text = course.name
        holder.type.text = course.type
        holder.details.text = course.details
        holder.duration.text = course.duration
    }

    override fun getItemCount(): Int = courses.size
}
