package com.example.tildau.navigation

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tildau.R
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.next.NextStepType

class CourseFlowCoordinator(
    private val navController: NavController
) {

    fun handleNextStep(nextStep: NextStepResponse) {
        when (nextStep.type) {

            NextStepType.EXERCISE,
            NextStepType.RETRY -> {

                val bundle = Bundle().apply {
                    putString("ARG_EXERCISE_ID", nextStep.id)
                }

                navController.navigate(
                    R.id.action_global_recordFragment,
                    bundle
                )
            }

            NextStepType.FINISH -> {
                Toast.makeText(
                    navController.context,
                    "Course completed 🎉",
                    Toast.LENGTH_LONG
                ).show()

                navController.navigate(R.id.coursesFragment)
            }

            NextStepType.RESOURCE -> {
                Toast.makeText(
                    navController.context,
                    "Resource not implemented",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // 🔥 НОВОЕ: единая точка перехода в Result
    fun openResult(bundle: Bundle) {
        navController.navigate(
            R.id.action_analyzeFragment_to_resultFragment,
            bundle
        )
    }
}