package com.example.tildau.ui.skills

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.tildau.databinding.ViewSkillsBinding

class SkillsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewSkillsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
        isClickable = true
        isFocusable = true
        foreground = context.obtainStyledAttributes(
            intArrayOf(android.R.attr.selectableItemBackground)
        ).getDrawable(0)
    }

    fun setData(
        fluency: Int,
        pronunciation: Int,
        overall: Int
    ) {
        binding.tvFluency.text = "Fluency: $fluency"
        binding.tvPronunciation.text = "Pronunciation: $pronunciation"
        binding.tvOverall.text = "Overall: $overall"

        setFill(binding.tvFluency, fluency)
        setFill(binding.tvPronunciation, pronunciation)
        setFill(binding.tvOverall, overall)
    }

    private fun setFill(view: android.view.View, value: Int) {
        val clamped = value.coerceIn(0, 100)
        view.background.level = clamped * 100
    }
}