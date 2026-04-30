package com.example.tildau.ui.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.databinding.ItemAchievementBinding

class AchievementAdapter :
    ListAdapter<AchievementType, AchievementAdapter.ViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AchievementType>() {
            override fun areItemsTheSame(oldItem: AchievementType, newItem: AchievementType) = oldItem == newItem
            override fun areContentsTheSame(oldItem: AchievementType, newItem: AchievementType) = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(type: AchievementType) {
            val data = AchievementRegistry.get(type)

            binding.tvTitle.text = data.title
            binding.tvDescription.text = data.description
            binding.ivIcon.setImageResource(data.iconRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}