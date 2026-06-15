package com.example.tildau.ui.courses

import com.example.tildau.R

object CourseCoverHelper {

    fun getBigCover(title: String): Int {

        return if (title.startsWith("Қосымша курс")) {
            R.drawable.cover_secondary
        } else {
            R.drawable.cover_main
        }
    }

    fun getSmallCover(title: String): Int {

        return if (title.startsWith("Қосымша курс")) {
            R.drawable.cover_secondary_small
        } else {
            R.drawable.cover_main_small
        }
    }
}