package com.example.tildau.navigation

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.next.NextStepType

object NextStepHandler {

    fun handle(fragment: Fragment, nextStep: NextStepResponse) {
        when (nextStep.type) {

            NextStepType.EXERCISE,
            NextStepType.RETRY -> {
                nextStep.id?.let { id ->
                    val bundle = Bundle().apply {
                        putString("ARG_EXERCISE_ID", id)
                    }

                    fragment.findNavController().navigate(
                        R.id.action_global_recordFragment,
                        bundle
                    )
                }
            }

            NextStepType.FINISH -> {
                Toast.makeText(fragment.requireContext(), "Course completed 🎉", Toast.LENGTH_LONG).show()
                fragment.findNavController().navigate(R.id.coursesFragment)
            }

            NextStepType.RESOURCE -> {
                Toast.makeText(fragment.requireContext(), "Resource not implemented", Toast.LENGTH_SHORT).show()
            }

            NextStepType.UNKNOWN -> {
                Toast.makeText(
                    fragment.requireContext(),
                    "Unknown step type",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}