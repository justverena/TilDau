package com.example.tildau.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tildau.R
import com.example.tildau.data.model.course.ExerciseResponse

class ExercisesAdapter(private var exercises: List<ExerciseResponse>) :
    RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.exerciseTitle)
        val instruction: TextView = itemView.findViewById(R.id.exerciseInstruction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_card, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.title.text = exercise.title
        holder.instruction.text = exercise.instruction
    }

    override fun getItemCount(): Int = exercises.size

    fun updateExercises(newExercises: List<ExerciseResponse>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}
