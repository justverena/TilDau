package com.example.tildau.ui.course

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.enums.UnitState

class CourseAdapter(
    private var items: MutableList<CourseItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_FEATURE = 1
        private const val TYPE_UNIT = 2
        private const val TYPE_EXERCISE = 3
        private const val TYPE_COURSE_CARD = 4
        private const val TYPE_INFO_ROW = 5
        private const val TYPE_PROGRESS_BOX = 6
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is CourseItem.Header -> TYPE_HEADER
        is CourseItem.Feature -> TYPE_FEATURE
        is CourseItem.Unit -> TYPE_UNIT
        is CourseItem.Exercise -> TYPE_EXERCISE
        is CourseItem.CourseCard -> TYPE_COURSE_CARD
        is CourseItem.InfoRow -> TYPE_INFO_ROW
        is CourseItem.ProgressBox -> TYPE_PROGRESS_BOX
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_course_header, parent, false))
            TYPE_FEATURE -> FeatureViewHolder(inflater.inflate(R.layout.item_feature, parent, false))
            TYPE_UNIT -> UnitViewHolder(inflater.inflate(R.layout.item_unit_card, parent, false))
            TYPE_EXERCISE -> ExerciseViewHolder(inflater.inflate(R.layout.item_exercise_card, parent, false))
            TYPE_COURSE_CARD -> CourseCardViewHolder(inflater.inflate(R.layout.item_course_card, parent, false))
            TYPE_INFO_ROW -> InfoRowViewHolder(inflater.inflate(R.layout.item_info_row, parent, false))
            TYPE_PROGRESS_BOX -> ProgressBoxViewHolder(inflater.inflate(R.layout.item_progress_box, parent, false))
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<CourseItem>) {
        android.util.Log.d("ADAPTER_DEBUG", "NEW ITEMS = ${newItems.size}")

        items.clear()
        items.addAll(newItems)

        notifyDataSetChanged()
    }

    private fun collapseAllUnits() {
        items.removeAll { it is CourseItem.Exercise }

        items.forEach {
            if (it is CourseItem.Unit) it.isExpanded = false
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CourseItem.Header -> (holder as HeaderViewHolder).bind(item)
            is CourseItem.Feature -> (holder as FeatureViewHolder).bind(item)
            is CourseItem.Unit -> (holder as UnitViewHolder).bind(item)
            is CourseItem.Exercise -> (holder as ExerciseViewHolder).bind(item)
            is CourseItem.CourseCard -> (holder as CourseCardViewHolder).bind()
            is CourseItem.InfoRow -> (holder as InfoRowViewHolder).bind(item)
            is CourseItem.ProgressBox -> (holder as ProgressBoxViewHolder).bind(item)
        }
    }

    // ===================== UNIT =====================
    inner class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val numberText: TextView = itemView.findViewById(R.id.unitNumber)
        private val titleText: TextView = itemView.findViewById(R.id.unitTitle)
        private val statusPrimary: TextView = itemView.findViewById(R.id.statusPrimary)
        private val statusSecondary: TextView = itemView.findViewById(R.id.statusSecondary)
        private val arrow: ImageView = itemView.findViewById(R.id.stateIcon)

        fun bind(unitItem: CourseItem.Unit) {

            android.util.Log.d(
                "UNIT_DEBUG",
                "bind unit=${unitItem.title} state=${unitItem.state} expanded=${unitItem.isExpanded}"
            )

            // ======================
            // RESET UI (ВАЖНО)
            // ======================
            statusSecondary.visibility = View.VISIBLE
            numberText.setBackgroundResource(R.drawable.bg_circle_idle)
            statusPrimary.setTextColor(Color.BLACK)
            statusPrimary.text = ""
            statusSecondary.text = ""

            // ======================
            // SET DATA
            // ======================
            numberText.text = unitItem.number.toString()
            titleText.text = unitItem.title

            bindUnitState(unitItem)

            arrow.rotation = if (unitItem.isExpanded) 180f else 0f

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                if (unitItem.state == UnitState.LOCKED) return@setOnClickListener

                if (unitItem.isExpanded) collapseUnit(unitItem, position)
                else expandUnit(unitItem, position)
            }
        }

        private fun bindUnitState(unitItem: CourseItem.Unit) {

            when (unitItem.state) {

                UnitState.LOCKED -> {
                    statusPrimary.text = "Locked"
                    statusSecondary.visibility = View.GONE
                    numberText.setBackgroundResource(R.drawable.bg_circle_idle)
                }

                UnitState.CURRENT -> {
                    statusPrimary.text = "In progress"
                    statusSecondary.visibility = View.VISIBLE
                    statusSecondary.text = "Progress: ${unitItem.progress ?: 0}%"
                    numberText.setBackgroundResource(R.drawable.bg_circle_current)
                }

                UnitState.COMPLETED -> {
                    statusPrimary.text = "Completed"
                    statusSecondary.visibility = View.VISIBLE
                    statusSecondary.text = "Last Score: ${unitItem.lastScore ?: 0}%"
                    numberText.setBackgroundResource(R.drawable.bg_circle_completed)
                }
            }
        }

        private fun collapseUnit(unitItem: CourseItem.Unit, position: Int) {
            var removeCount = 0
            var index = position + 1

            while (index < items.size) {
                val item = items[index]

                if (item is CourseItem.Exercise &&
                    item.parentUnitNumber == unitItem.number
                ) {
                    items.removeAt(index)
                    removeCount++
                } else break
            }

            notifyItemRangeRemoved(position + 1, removeCount)
            unitItem.isExpanded = false
            notifyItemChanged(position)
        }

        private fun expandUnit(unitItem: CourseItem.Unit, position: Int) {

            collapseAllUnits()

            val newPosition = items.indexOfFirst {
                it is CourseItem.Unit && it.number == unitItem.number
            }

            if (newPosition == -1) return

            val exercises = unitItem.unitResponse.exercises
                .map { CourseItem.Exercise(it, unitItem.number) }

            items.addAll(newPosition + 1, exercises)

            notifyItemRangeInserted(newPosition + 1, exercises.size)

            unitItem.isExpanded = true
            notifyItemChanged(newPosition)
        }
    }

    // ===================== EXERCISE =====================
    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.exerciseTitle)

        fun bind(item: CourseItem.Exercise) {
            title.text = item.exerciseResponse.title
            itemView.alpha = if (item.exerciseResponse.isLocked) 0.5f else 1f
            itemView.isEnabled = !item.exerciseResponse.isLocked
        }
    }

    // ===================== HEADER =====================
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CourseItem.Header) {
            itemView.findViewById<TextView>(R.id.titleText).text = item.title
            itemView.findViewById<TextView>(R.id.descriptionText).text = item.description
        }
    }

    // ===================== FEATURE =====================
    inner class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CourseItem.Feature) {
            itemView.findViewById<TextView>(R.id.featureText).text = item.text
        }
    }

    // ===================== COURSE CARD =====================
    inner class CourseCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.courseCardTitle)

        fun bind() {
            title.text = "Дислалия"
        }
    }

    // ===================== INFO ROW =====================
    inner class InfoRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sections: TextView = itemView.findViewById(R.id.sectionsText)

        fun bind(item: CourseItem.InfoRow) {
            sections.text = "${item.sections} Sections"
        }
    }

    // ===================== PROGRESS BOX =====================
    inner class ProgressBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val progressPercent: TextView = itemView.findViewById(R.id.progressPercent)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val resumeText: TextView = itemView.findViewById(R.id.resumeText)

        fun bind(item: CourseItem.ProgressBox) {

            val value = item.progressPercent.toInt().coerceIn(0, 100)

            progressPercent.text = "$value%"

            progressBar.progress = 0
            progressBar.post {
                progressBar.progress = value
            }

            resumeText.text = item.resumeText

            android.util.Log.d("PROGRESS_DEBUG", "progress=$value")
        }
    }
}