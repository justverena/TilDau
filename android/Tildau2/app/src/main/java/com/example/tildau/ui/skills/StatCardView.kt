package com.example.tildau.ui.skills

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.tildau.R
import com.example.tildau.databinding.ViewStatCardBinding
import com.google.android.material.card.MaterialCardView

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Заполняет карточку данными
     * @param progress Int - процент прогресса 0..100
     * @param title String - название скилла
     * @param backgroundColor Int? - цвет фона карточки (опционально)
     */
    fun bind(progress: Int, title: String, backgroundColor: Int? = null) {
        binding.progressIndicator.progress = progress
        binding.tvProgress.text = progress.toString()
        binding.tvTitle.text = title

        backgroundColor?.let { setCardBackgroundColor(ContextCompat.getColor(context, it)) }
    }
}