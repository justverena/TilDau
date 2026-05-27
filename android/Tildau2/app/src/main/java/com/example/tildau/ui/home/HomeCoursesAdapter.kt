package com.example.tildau.ui.home.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.model.course.CourseShortResponse

class HomeCoursesAdapter(
    private var courses: List<CourseShortResponse>,
    private val onCourseClick: (CourseShortResponse) -> Unit
) : RecyclerView.Adapter<HomeCoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_course, parent, false)
        ) {

        val title: TextView =
            itemView.findViewById(R.id.tvCourseTitle)

        val type: TextView =
            itemView.findViewById(R.id.tvCourseType)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseViewHolder {
        return CourseViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: CourseViewHolder,
        position: Int
    ) {

        val course = courses[position]

        holder.title.text = course.title

        // 🔥 TEMP TYPE
        holder.type.text = "Speech Therapy"

        holder.itemView.setOnClickListener {
            onCourseClick(course)
        }
    }

    override fun getItemCount(): Int = courses.size

    fun updateCourses(newCourses: List<CourseShortResponse>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}