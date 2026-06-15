package com.example.tildau.ui.skills

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.tildau.R
import com.example.tildau.databinding.ViewStatCardBinding
import com.google.android.material.card.MaterialCardView

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding: ViewStatCardBinding

    init {
        val inflater = LayoutInflater.from(context)

        // ❗ ВАЖНО: правильная сигнатура inflate
        binding = ViewStatCardBinding.inflate(inflater, this)

        radius = 32f
        cardElevation = 0f
    }

    fun setData(
        title: String,
        value: Int,
        isActive: Boolean
    ) {
        binding.tvTitle.text = title
        binding.tvValue.text = value.toString()
        binding.progressBar.progress = value

        // ❌ УБРАТЬ всю логику серого цвета
        binding.tvValue.setTextColor(
            ContextCompat.getColor(context, R.color.black)
        )
    }

    fun setCardBackgroundResource(drawableRes: Int) {
        background =
            ContextCompat.getDrawable(
                context,
                drawableRes
            )
    }
}