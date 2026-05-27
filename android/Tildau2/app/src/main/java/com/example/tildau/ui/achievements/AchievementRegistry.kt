package com.example.tildau.ui.achievements

import com.example.tildau.R

object AchievementRegistry {

    fun get(type: AchievementType): Achievement {
        return when (type) {

            // 🔥 STREAK
            AchievementType.STREAK_3 -> Achievement(
                title = "Тұрақты бастау",
                description = "3 күн қатарынан сөйлеу сапасы жақсы болды",
                iconRes = R.drawable.ic_streak_start
            )

            AchievementType.STREAK_7 -> Achievement(
                title = "Бірқалыпты қарқын",
                description = "7 күн қатарынан сөйлеу қарқыны тұрақты",
                iconRes = R.drawable.ic_streak_week
            )

            AchievementType.STREAK_14 -> Achievement(
                title = "Жүктемедегі бақылау",
                description = "14 күн қатарынан жоғары сапада сөйлеу",
                iconRes = R.drawable.ic_streak_long
            )

            // 📈 SKILL
            AchievementType.GOOD_SCORE_90 -> Achievement(
                title = "Айқын прогресс",
                description = "Бір жаттығуда 90 және одан жоғары балл жинады",
                iconRes = R.drawable.ic_growth_start
            )

            AchievementType.GOOD_SCORE_90_3 -> Achievement(
                title = "Дәлдікке фокус",
                description = "Қатарынан 3 жаттығуда 90 және одан жоғары балл",
                iconRes = R.drawable.ic_growth_precision
            )

            AchievementType.GOOD_SCORE_90_5 -> Achievement(
                title = "Жоғары сөйлеу деңгейі",
                description = "Қатарынан 5 жаттығуда 90 және одан жоғары балл",
                iconRes = R.drawable.ic_growth_high
            )

            // 🎓 UNIT
            AchievementType.UNIT_90 -> Achievement(
                title = "Сенімді бөлім",
                description = "Бөлім бойынша орташа балл 90 және одан жоғары",
                iconRes = R.drawable.ic_unit_complete
            )

            AchievementType.UNIT_95 -> Achievement(
                title = "Таза аяқтау",
                description = "Бөлім бойынша орташа балл 95 және одан жоғары",
                iconRes = R.drawable.ic_unit_perfect
            )

            // 🏁 COURSE
            AchievementType.COURSE_90 -> Achievement(
                title = "Шебер деңгей",
                description = "Курсты орташа балл 90 және одан жоғары аяқтау",
                iconRes = R.drawable.ic_mastery
            )
        }
    }
}