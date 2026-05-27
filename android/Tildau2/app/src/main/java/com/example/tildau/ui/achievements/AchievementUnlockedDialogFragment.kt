package com.example.tildau.ui.achievements

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.tildau.R
import com.example.tildau.data.model.stats.AchievementResponse

class AchievementUnlockedDialogFragment(
    private val achievement: AchievementResponse
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.dialog_achievement_unlocked,
                null
            )

        val icon = view.findViewById<ImageView>(R.id.ivIcon)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val desc = view.findViewById<TextView>(R.id.tvDescription)
        val button = view.findViewById<Button>(R.id.btnContinue)

        title.text = achievement.title
        desc.text = achievement.description

        icon.setImageResource(
            mapIcon(achievement.code)
        )

        button.setOnClickListener {
            dismiss()
        }

        val dialog = Dialog(requireContext())

        dialog.setContentView(view)

        dialog.window?.apply {

            setBackgroundDrawableResource(android.R.color.transparent)

            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        return dialog
    }

    private fun mapIcon(code: String): Int {

        return when (code) {

            "STREAK_3" -> R.drawable.ic_streak_start
            "STREAK_7" -> R.drawable.ic_streak_week
            "STREAK_14" -> R.drawable.ic_streak_long

            "GOOD_SCORE_90" -> R.drawable.ic_growth_start
            "GOOD_SCORE_90_3" -> R.drawable.ic_growth_precision
            "GOOD_SCORE_90_5" -> R.drawable.ic_growth_high

            "UNIT_90" -> R.drawable.ic_unit_complete
            "UNIT_95" -> R.drawable.ic_unit_perfect

            "COURSE_90" -> R.drawable.ic_mastery

            else -> R.drawable.ic_mastery
        }
    }
}