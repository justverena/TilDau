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

    /** Передаём TextView, который будет показывать "Exercise X of Y" */
    fun setProgressTextView(textView: TextView) {
        this.progressTextView = textView
    }

    /** Создаём N точек */
    fun setup(count: Int) {
        removeAllViews()
        items.clear()
        maxExercises = count

        repeat(count) { index ->
            val view = View(context).apply {
                val size = resources.getDimensionPixelSize(R.dimen.progress_circle_size)
                layoutParams = LayoutParams(size, size).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.progress_circle_margin)
                }
                background = ContextCompat.getDrawable(context, R.drawable.progress_item_base)
            }
            items.add(view)
            addView(view)
        }
    }

    /** Обновляем текущий индекс и текст */
    fun update(currentIndex: Int) {
        items.forEachIndexed { index, view ->
            val drawable = view.background as GradientDrawable
            when {
                index < currentIndex -> { // Завершено / активное
                    drawable.setColor(ContextCompat.getColor(context, R.color.main_purple))
                    drawable.setStroke(0, Color.TRANSPARENT)
                }
                index == currentIndex -> { // Текущее
                    drawable.setColor(ContextCompat.getColor(context, R.color.light_purple))
                    drawable.setStroke(
                        resources.getDimensionPixelSize(R.dimen.progress_circle_stroke),
                        ContextCompat.getColor(context, R.color.main_purple)
                    )
                }
                else -> { // Ожидающее
                    drawable.setColor(ContextCompat.getColor(context, R.color.light_purple))
                    drawable.setStroke(0, Color.TRANSPARENT)
                }
            }
        }

        // 🔹 Обновляем текст: "Exercise X of Y"
        progressTextView?.text = "Exercise ${currentIndex + 1} of $maxExercises"
    }
}