package com.example.tildau.ui.home.courses

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.model.course.CourseShortResponse

class YourCoursesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val rvCourses: RecyclerView
    private val tvViewMore: TextView

    private lateinit var adapter: HomeCoursesAdapter

    init {

        orientation = VERTICAL

        inflate(context, R.layout.view_home_courses, this)

        rvCourses = findViewById(R.id.rvCourses)
        tvViewMore = findViewById(R.id.tvViewMore)

        setupRecycler()
    }

    private fun setupRecycler() {

        adapter = HomeCoursesAdapter(emptyList()) {

            onCourseClicked?.invoke(it)
        }

        rvCourses.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvCourses.adapter = adapter
    }

    fun submitCourses(
        courses: List<CourseShortResponse>
    ) {
        adapter.updateCourses(courses)
    }

    // -------------------------
    // NAVIGATION CALLBACKS
    // -------------------------

    var onViewMoreClicked: (() -> Unit)? = null

    var onCourseClicked: ((CourseShortResponse) -> Unit)? = null

    init {

        tvViewMore.setOnClickListener {
            onViewMoreClicked?.invoke()
        }
    }
}