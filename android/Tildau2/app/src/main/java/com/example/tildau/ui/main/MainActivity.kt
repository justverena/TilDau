package com.example.tildau.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.databinding.ActivityMainBinding
import com.example.tildau.navigation.CourseFlowCoordinator
import com.example.tildau.ui.common.TapBarController
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var coordinator: CourseFlowCoordinator
    private lateinit var tapBarController: TapBarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("TAPBAR_DEBUG", "Prefs ALL = ${getSharedPreferences("app_prefs", MODE_PRIVATE).all}")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // -----------------------------
        // Core architecture layer
        // -----------------------------
        coordinator = CourseFlowCoordinator(navController)
        tapBarController = TapBarController(navController)

        setupTapBar()
    }

    // -----------------------------
    // TAPBAR = UI EVENT LAYER ONLY
    // -----------------------------
    private fun setupTapBar() {

        binding.tapbar.btnHome.setOnClickListener {
            tapBarController.onHomeClicked()
        }

        binding.tapbar.btnLesson.setOnClickListener {
            tapBarController.onLessonClicked()
        }

        binding.tapbar.btnProfile.setOnClickListener {
            tapBarController.onProfileClicked()
        }

//        binding.tapbar.btnStart.setOnClickListener {
//            val courseId = getCurrentCourseId()
//
//            tapBarController.onStartClicked(courseId) {
//                startOrResumeCourse()
//            }
//        }

        binding.tapbar.btnStart.setOnClickListener {
            Log.d("TAPBAR_DEBUG", "START CLICKED")

            val courseId = getCurrentCourseId()
            Log.d("TAPBAR_DEBUG", "courseId = $courseId")

            tapBarController.onStartClicked(courseId) {
                Log.d("TAPBAR_DEBUG", "CALLING startOrResumeCourse")
                startOrResumeCourse()
            }
        }
    }



    // -----------------------------
    // COURSE FLOW ORCHESTRATION
    // -----------------------------
    private fun startOrResumeCourse() {

        val courseId = getCurrentCourseId()

        if (courseId == null) {
            Toast.makeText(this, "Курс таңдалған жоқ", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {

            try {
                val api = ApiClient.createServiceWithToken(
                    CourseApi::class.java
                ) { TokenManager.getToken(this@MainActivity) }

                val repository = CourseRepository(api)

                val nextStep =
                    repository.resumeCourse(courseId)
                        ?: repository.startCourse(courseId)

                if (nextStep != null) {
                    coordinator.handleNextStep(nextStep)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    e.message ?: "Unknown error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // -----------------------------
    // DATA
    // -----------------------------
    private fun getCurrentCourseId(): String? {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val id = prefs.getString("current_course_id", null)

        Log.d("TAPBAR_DEBUG", "READ courseId from prefs = $id")

        return id
    }
}