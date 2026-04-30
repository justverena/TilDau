package com.example.tildau.ui.achievements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.example.tildau.databinding.ItemAchievementBinding

class AchievementView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = ItemAchievementBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var isExpanded = false

    fun bind(type: AchievementType) {
        val data = AchievementRegistry.get(type)

        binding.tvTitle.text = data.title
        binding.tvDescription.text = data.description
        binding.ivIcon.setImageResource(data.iconRes)
    }

    fun toggle() {
        isExpanded = !isExpanded
        binding.tvDescription.visibility =
            if (isExpanded) View.VISIBLE else View.GONE
    }
}