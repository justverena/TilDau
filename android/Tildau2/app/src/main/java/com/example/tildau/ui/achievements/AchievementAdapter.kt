package com.example.tildau.ui.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.databinding.ItemAchievementBinding
import com.example.tildau.R
import com.example.tildau.data.model.stats.AchievementResponse

class AchievementAdapter :
    ListAdapter<AchievementResponse, AchievementAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AchievementResponse>() {
            override fun areItemsTheSame(oldItem: AchievementResponse, newItem: AchievementResponse) =
                oldItem.code == newItem.code

            override fun areContentsTheSame(oldItem: AchievementResponse, newItem: AchievementResponse) =
                oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AchievementResponse) {

            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description

            binding.ivIcon.setImageResource(mapIcon(item.code))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 🔥 BACKEND CODE → ICON
    private fun mapIcon(code: String): Int {
        val type = runCatching { AchievementType.valueOf(code) }.getOrNull()

        return when (type) {

            AchievementType.STREAK_3 -> R.drawable.ic_streak_start
            AchievementType.STREAK_7 -> R.drawable.ic_streak_week
            AchievementType.STREAK_14 -> R.drawable.ic_streak_long

            AchievementType.GOOD_SCORE_90 -> R.drawable.ic_growth_start
            AchievementType.GOOD_SCORE_90_3 -> R.drawable.ic_growth_precision
            AchievementType.GOOD_SCORE_90_5 -> R.drawable.ic_growth_high

            AchievementType.UNIT_90 -> R.drawable.ic_unit_complete
            AchievementType.UNIT_95 -> R.drawable.ic_unit_perfect

            AchievementType.COURSE_90 -> R.drawable.ic_mastery

            null -> R.drawable.ic_mastery
        }
    }
}