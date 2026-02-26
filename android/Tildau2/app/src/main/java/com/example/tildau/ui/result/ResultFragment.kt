package com.example.tildau.ui.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.tildau.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class ResultFragment : Fragment() {

    private lateinit var circleProgress: CircularProgressIndicator
    private lateinit var scoreNumber: TextView
    private lateinit var resultStatus: TextView
    private lateinit var nextLessonBtn: Button
    private lateinit var retryPracticeBtn: Button
    private lateinit var backButton: ImageView
    private lateinit var feedbackContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        // Инициализация всех view
        circleProgress = view.findViewById(R.id.circle_progress)
        scoreNumber = view.findViewById(R.id.score_number)
        resultStatus = view.findViewById(R.id.result_status)
        nextLessonBtn = view.findViewById(R.id.next_lesson_btn)
        retryPracticeBtn = view.findViewById(R.id.retry_practice_btn)
        backButton = view.findViewById(R.id.back_button)
        feedbackContainer = view.findViewById(R.id.feedback_container)

        setupViews()

        return view
    }

    private fun setupViews() {
        val score = arguments?.getInt("score") ?: 0
        val feedback = arguments?.getStringArrayList("feedback") ?: arrayListOf()

        // Настраиваем прогресс и текст
        circleProgress.progress = score
        scoreNumber.text = score.toString()

        resultStatus.text = when {
            score >= 90 -> "Excellent"
            score >= 75 -> "Good"
            score >= 50 -> "Average"
            else -> "Try Again"
        }

        // Очистка контейнера на всякий случай
        feedbackContainer.removeAllViews()

        // Динамическое добавление карточек
        feedback.forEach { text ->
            val card = layoutInflater.inflate(R.layout.view_insight_card, feedbackContainer, false)
            val insightText = card.findViewById<TextView>(R.id.insight_text)
            insightText.text = text
            feedbackContainer.addView(card)
        }

        // Кнопки
        nextLessonBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Next Lesson clicked", Toast.LENGTH_SHORT).show()
        }

        retryPracticeBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Retry Practice clicked", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}