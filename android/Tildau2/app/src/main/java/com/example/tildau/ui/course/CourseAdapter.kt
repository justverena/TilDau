package com.example.tildau.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.enums.UnitState
import com.example.tildau.data.model.course.UnitResponse
import com.example.tildau.ui.unit.UnitFragment
import com.google.android.material.card.MaterialCardView

class CourseAdapter(
    private var items: List<CourseItem>,
    private val onUnitClick: ((UnitResponse) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_FEATURE = 1
        private const val TYPE_UNIT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CourseItem.Header -> TYPE_HEADER
            is CourseItem.Feature -> TYPE_FEATURE
            is CourseItem.Unit -> TYPE_UNIT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_course_header, parent, false))
            TYPE_FEATURE -> FeatureViewHolder(inflater.inflate(R.layout.item_feature, parent, false))
            TYPE_UNIT -> UnitViewHolder(inflater.inflate(R.layout.item_unit_card, parent, false), onUnitClick)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as CourseItem.Header)
            is FeatureViewHolder -> holder.bind(items[position] as CourseItem.Feature)
            is UnitViewHolder -> holder.bind(items[position] as CourseItem.Unit)
        }
    }

    fun updateItems(newItems: List<CourseItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CourseItem.Header) {
            itemView.findViewById<TextView>(R.id.titleText).text = item.title
            itemView.findViewById<TextView>(R.id.descriptionText).text = item.description
        }
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CourseItem.Feature) {
            itemView.findViewById<TextView>(R.id.featureText).text = item.text
        }
    }

    class UnitViewHolder(
        itemView: View,
        private val onUnitClick: ((UnitResponse) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {

        private val statusPrimary: TextView = itemView.findViewById(R.id.statusPrimary)
        private val statusSecondary: TextView = itemView.findViewById(R.id.statusSecondary)
        private val stateIcon: ImageView = itemView.findViewById(R.id.stateIcon)
        private val card: MaterialCardView = itemView.findViewById(R.id.unitCard)

        private val numberText: TextView = itemView.findViewById(R.id.unitNumber)
        private val titleText: TextView = itemView.findViewById(R.id.unitTitle)
//        private val descriptionText: TextView = itemView.findViewById(R.id.unitDescription)

        fun bind(item: CourseItem.Unit) {

            numberText.text = item.number.toString()
            titleText.text = item.title

            when (item.state) {

                UnitState.LOCKED -> {

                    card.strokeColor =
                        ContextCompat.getColor(itemView.context, R.color.grey_border)

                    statusPrimary.text = "Locked"
                    statusSecondary.visibility = View.VISIBLE

                    stateIcon.setImageResource(R.drawable.ic_lock)
                    stateIcon.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.grey_icon)
                    )

                    itemView.isEnabled = true
                }

                UnitState.CURRENT -> {

                    card.strokeColor =
                        ContextCompat.getColor(itemView.context, R.color.main_purple)

                    statusPrimary.text = "Continues"
                    statusSecondary.visibility = View.VISIBLE
                    statusSecondary.text = "Progress: ${item.progress ?: 0}%"

                    stateIcon.setImageResource(R.drawable.ic_play)
                    stateIcon.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.main_purple)
                    )

                    itemView.isEnabled = true
                }

                UnitState.COMPLETED -> {

                    card.strokeColor =
                        ContextCompat.getColor(itemView.context, R.color.green_complete)

                    statusPrimary.text = "Completed"
                    statusSecondary.visibility = View.VISIBLE
                    statusSecondary.text = "Last Score: ${item.lastScore ?: 0}%"

                    stateIcon.setImageResource(R.drawable.ic_check)
                    stateIcon.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.green_complete)
                    )

                    itemView.isEnabled = true
                }
            }

            itemView.setOnClickListener {
                if (item.state != UnitState.LOCKED) {
                    val bundle = Bundle()
                    bundle.putSerializable(UnitFragment.ARG_UNIT, item.unitResponse)
                    androidx.navigation.Navigation.findNavController(itemView)
                        .navigate(R.id.action_courseFragment_to_unitFragment, bundle)
                }
            }
        }

    }
}
