package com.example.tildau.ui.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.stats.AchievementResponse
import com.example.tildau.navigation.CourseFlowCoordinator
import com.example.tildau.ui.achievements.AchievementUnlockedDialogFragment
import android.widget.ProgressBar
class ResultFragment : Fragment() {

    private var nextStep: NextStepResponse? = null
    private var exerciseId: String? = null

    private lateinit var circleProgress: ProgressBar
    private lateinit var scoreNumber: TextView
    private lateinit var resultStatus: TextView
    private lateinit var nextLessonBtn: Button
    private lateinit var retryPracticeBtn: Button

    private lateinit var feedbackText: TextView

    private val coordinator by lazy {
        CourseFlowCoordinator(findNavController())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_result, container, false)

        circleProgress = view.findViewById(R.id.circle_progress)
        scoreNumber = view.findViewById(R.id.score_number)
        resultStatus = view.findViewById(R.id.result_status)
        nextLessonBtn = view.findViewById(R.id.next_lesson_btn)
        retryPracticeBtn = view.findViewById(R.id.retry_practice_btn)
        feedbackText = view.findViewById(R.id.tvFeedbackText)

        bindData()
        setupClicks()

        return view
    }

    private fun bindData() {



        val args = arguments ?: return

        val score = args.getInt("score", 0)

        val passed = score >= 85

        exerciseId = args.getString("exerciseId")

        @Suppress("DEPRECATION")
        nextStep = args.getSerializable("nextStep") as? NextStepResponse

        val achievements =
            args.getSerializable("achievements") as? ArrayList<AchievementResponse>
                ?: arrayListOf()

        Log.d("RESULT_DEBUG", "nextStep = $nextStep")
        Log.d("RESULT_DEBUG", "exerciseId = $exerciseId")
        Log.d(
            "RESULT_DEBUG",
            "score=$score, nextStep=$nextStep"
        )

        // ---------------- UI ----------------

        circleProgress.progress = score
        scoreNumber.text = score.toString()

        if (passed) {

            // Прошёл упражнение
            retryPracticeBtn.visibility = View.GONE

            nextLessonBtn.isEnabled = true
            nextLessonBtn.alpha = 1f

        } else {

            // Не прошёл упражнение
            nextLessonBtn.isEnabled = false
            nextLessonBtn.alpha = 0.5f

            retryPracticeBtn.visibility = View.VISIBLE
        }

        if (score < 85) {
            nextLessonBtn.isEnabled = false
            nextLessonBtn.alpha = 0.5f
        }

        resultStatus.text = when {
            score >= 90 -> "Керемет"
            score >= 75 -> "Жақсы"
            score >= 50 -> "Орташа"
            else -> "Қайта көріңіз"
        }

        feedbackText.text = when {

            // Пасхалка
            score == 67 -> {
                "Дәл 67 ұпай...\n" +
                        "Кездейсоқтық па, әлде әдейі ме? " +
                        "Қалай болғанда да, тағы бір рет байқап көр. Сен бұдан да жоғары нәтиже көрсете аласың."
            }

            // Идеальный результат
            score == 100 -> {
                "Мінсіз нәтиже!\n" +
                        "Сіз бұл тапсырманы қатесіз орындадыңыз. " +
                        "Бұл ең жоғары мүмкін балл. Келесі жаттығуға сенімді түрде өте аласыз."
            }

            // Проходной балл и выше
            score >= 90 -> {
                "Өте жақсы нәтиже!\n" +
                        "Сөйлеу дағдыларыңыз жоғары деңгейде. " +
                        "Келесі жаттығуға өтуге болады."
            }

            // Проходной балл
            score >= 85 -> {
                "Тапсырма сәтті аяқталды.\n" +
                        "Сіз өту баллын жинадыңыз. Кейбір тұстарды жақсартуға болады, бірақ келесі жаттығуға өте аласыз."
            }

            // Ниже проходного
            score >= 70 -> {
                "Жақсы әрекет.\n" +
                        "Негізгі материалды меңгердіңіз, бірақ нәтижені жақсарту үшін жаттығуды қайта орындаған жөн."
            }

            // Очень низкий
            else -> {
                "Бұл тапсырманы қайта орындау қажет.\n" +
                        "Өту баллына жеткен жоқсыз. Келесі жаттығуға өту үшін осы тапсырманы қайта орындаңыз."
            }
        }


        if (achievements.isNotEmpty()) {
            AchievementUnlockedDialogFragment(achievements.first())
                .show(parentFragmentManager, "achievement_dialog")
        }

        // disable button if no next step
        if (nextStep == null) {
            Log.e("RESULT_DEBUG", "nextStep is NULL")
            nextLessonBtn.isEnabled = false
            nextLessonBtn.alpha = 0.5f
        }
    }

    private fun setupClicks() {

        nextLessonBtn.setOnClickListener {

            val step = nextStep

            if (step == null) {
                Toast.makeText(requireContext(), "Next step missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("RESULT_DEBUG", "NEXT CLICK type=${step.type}, id=${step.id}")

            coordinator.handleNextStep(step)
        }

        retryPracticeBtn.setOnClickListener {

            val step = nextStep

            if (step == null) {

                Toast.makeText(
                    requireContext(),
                    "Next step missing",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Log.d(
                "RESULT_DEBUG",
                "RETRY CLICK type=${step.type}, id=${step.id}"
            )

            coordinator.handleNextStep(step)
        }
    }
}