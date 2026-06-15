package com.example.tildau.ui.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.databinding.ViewAchievementBinding

class AchievementCarouselAdapter(
    private val items: List<AchievementType>
) : RecyclerView.Adapter<AchievementCarouselAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ViewAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(type: AchievementType) {

            val achievement =
                AchievementRegistry.get(type)

            binding.ivIcon.setImageResource(
                achievement.iconRes
            )

            binding.tvTitle.text =
                achievement.title
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ViewAchievementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}