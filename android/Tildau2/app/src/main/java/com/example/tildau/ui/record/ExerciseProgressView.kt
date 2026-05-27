package com.example.tildau.ui.record

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.tildau.R

class ExerciseProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val items = mutableListOf<View>()
    private var progressTextView: TextView? = null
    private var maxExercises = 0

    init {
        orientation = HORIZONTAL
    }
}