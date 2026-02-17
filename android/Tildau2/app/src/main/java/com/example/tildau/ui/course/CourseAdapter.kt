package com.example.tildau.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.model.course.UnitResponse

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
            TYPE_UNIT -> UnitViewHolder(inflater.inflate(R.layout.item_section_card, parent, false), onUnitClick)
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

        private val numberText: TextView = itemView.findViewById(R.id.sectionNumber)
        private val titleText: TextView = itemView.findViewById(R.id.sectionTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.sectionDescription)

        fun bind(item: CourseItem.Unit) {
            numberText.text = item.number.toString()
            titleText.text = item.title
            descriptionText.text = item.description

            itemView.setOnClickListener {
                onUnitClick?.invoke(item.unitResponse)
            }
        }
    }
}
