package com.example.tildau.ui.common

import androidx.navigation.NavController
import com.example.tildau.R

class TapBarController(
    private val navController: NavController
) {

    fun onStatsClicked() {
        navController.navigate(R.id.statisticsFragment)
    }

    fun onLessonClicked() {
        navController.navigate(R.id.coursesFragment)
    }

    fun onProfileClicked() {
        navController.navigate(R.id.accountFragment)
    }

    fun onStartClicked(courseId: String?, onStartCourse: () -> Unit) {
        if (courseId == null) {
            return
        }
        onStartCourse()
    }

    fun onHomeClicked() {
        navController.navigate(R.id.homeFragment)
    }
}