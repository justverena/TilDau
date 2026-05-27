package com.example.tildau.ui.achievements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.tildau.R
import com.example.tildau.databinding.ViewHomeAchievementsBinding

class HomeAchievementsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding =
        ViewHomeAchievementsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
    }

    fun submitList(types: List<AchievementType>) {

        val container = binding.container
        container.removeAllViews()

        types.forEachIndexed { index, type ->

            val data = AchievementRegistry.get(type)

            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_achievement, container, false)

            val icon = view.findViewById<ImageView>(R.id.ivIcon)
            val title = view.findViewById<TextView>(R.id.tvTitle)
            val desc = view.findViewById<TextView>(R.id.tvDescription)

            title.text = data.title
            desc.text = data.description
            icon.setImageResource(data.iconRes)

            val row = index / 2
            val col = index % 2

            val params = GridLayout.LayoutParams(
                GridLayout.spec(row),
                GridLayout.spec(col, 1f)
            ).apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(dp(6), dp(6), dp(6), dp(6))
            }

            view.layoutParams = params
            container.addView(view)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}