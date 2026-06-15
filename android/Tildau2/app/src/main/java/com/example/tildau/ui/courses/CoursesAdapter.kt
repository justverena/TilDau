package com.example.tildau.ui.courses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.model.course.CourseShortResponse

class CoursesAdapter(
    private var courses: List<CourseShortResponse>,
    private val onCourseClick: (CourseShortResponse) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView =
            view.findViewById(R.id.courseTitle)

        val image: ImageView =
            view.findViewById(R.id.courseImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }


    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]

        holder.title.text = course.title

        val imageView = holder.itemView.findViewById<ImageView>(R.id.courseImage)

        imageView.setImageResource(
            CourseCoverHelper.getSmallCover(course.title)
        )

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
