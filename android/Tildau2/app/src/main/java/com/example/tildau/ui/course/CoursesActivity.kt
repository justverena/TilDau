package com.example.tildau.ui.course

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.ui.base.BaseActivity
import com.example.tildau.data.local.TokenManager

class CoursesActivity : BaseActivity() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContent(R.layout.activity_courses)

        recyclerView = findViewById(R.id.recyclerViewCourses)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        courseAdapter = CourseAdapter(emptyList())
        recyclerView.adapter = courseAdapter

        val courseApi = ApiClient.createServiceWithToken(
            CourseApi::class.java
        ) {
            TokenManager.getToken(this)
        }


        val repository = CourseRepository(courseApi)
        val factory = CourseViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]

        observeViewModel()

        viewModel.loadCourses()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.courses.observe(this) { courses ->
            courseAdapter.updateCourses(courses)
        }

        viewModel.error.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
