package com.example.tildau.ui.achievements

import com.example.tildau.R

object AchievementRegistry {

    fun get(type: AchievementType): Achievement {
        return when (type) {

            // 🔥 STRIKES

            AchievementType.STABLE_START -> Achievement(
                title = "Stable Start",
                description = "Practice consistently for several days with solid performance",
                iconRes = R.drawable.ic_streak_start
            )

            AchievementType.STEADY_PACE -> Achievement(
                title = "Steady Pace",
                description = "Keep a full week streak with stable fluency",
                iconRes = R.drawable.ic_streak_week
            )

            AchievementType.CONTROL_UNDER_LOAD -> Achievement(
                title = "Control Under Load",
                description = "Maintain a long streak without losing quality",
                iconRes = R.drawable.ic_streak_long
            )

            // 📈 SKILL GROWTH

            AchievementType.CLEAR_PROGRESS -> Achievement(
                title = "Clear Progress",
                description = "Reach a high score in a single exercise",
                iconRes = R.drawable.ic_growth_start
            )

            AchievementType.PRECISION_FOCUS -> Achievement(
                title = "Precision Focus",
                description = "Achieve high results multiple times in a row",
                iconRes = R.drawable.ic_growth_precision
            )

            AchievementType.HIGH_STANDARD -> Achievement(
                title = "High Standard",
                description = "Maintain a consistently high level of performance",
                iconRes = R.drawable.ic_growth_high
            )

            // 🎓 COMPLETION

            AchievementType.CONFIDENT_UNIT -> Achievement(
                title = "Confident Unit",
                description = "Complete a unit with a strong average score",
                iconRes = R.drawable.ic_unit_complete
            )

            AchievementType.CLEAN_FINISH -> Achievement(
                title = "Clean Finish",
                description = "Finish a unit with excellent performance",
                iconRes = R.drawable.ic_unit_perfect
            )

            AchievementType.MASTERY -> Achievement(
                title = "Mastery",
                description = "Complete the entire course",
                iconRes = R.drawable.ic_mastery
            )
        }
    }
}