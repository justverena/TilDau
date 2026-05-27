package com.example.tildau.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.StatisticsApi
import com.example.tildau.databinding.FragmentStatisticsBinding
import com.example.tildau.ui.calendar.CalendarDay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import androidx.core.content.ContextCompat

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        val api = ApiClient.createServiceWithToken(
            StatisticsApi::class.java
        ) {
            TokenManager.getToken(requireContext())
        }
        com.example.tildau.data.repository.StatisticsRepository(api)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStatisticsFromBackend()
    }

    // -------------------------
    // 🔥 MAIN BACKEND LOGIC
    // -------------------------
    private fun loadStatisticsFromBackend() {
        lifecycleScope.launch {
            try {
                val streak = repository.getStreak()
                val stats = repository.getSkillTrend()

                Log.d("STATS", "fluency=${stats.fluency}")
                Log.d("STATS", "overall=${stats.overall}")
                Log.d("STATS", "pronunciation=${stats.pronunciation}")
                val calendar = repository.getCalendar()

                // STREAK
                binding.tvStreak.text = "Practiced $streak days in a row!"

//                // SKILLS
                binding.cardFluency.setData(
                    title = "Fluency",
                    value = stats.fluency.toInt(),
                    isActive = true
                )

                binding.cardOverall.setData(
                    title = "Overall",
                    value = stats.overall.toInt(),
                    isActive = false
                )

                binding.cardPronunciation.setData(
                    title = "Pronunciation",
                    value = stats.pronunciation.toInt(),
                    isActive = false
                )

                binding.cardOverall.setCardColor(
                    ContextCompat.getColor(requireContext(), R.color.bg_light_purple)
                )
//                setSkillCard(binding.tvFluencyValue, stats.fluency, true)
//                setSkillCard(binding.tvOverallValue, stats.overall, false)
//                setSkillCard(binding.tvPronunciationValue, stats.pronunciation, false)
//
//                setSkillProgress(binding.progressFluency, stats.fluency)
//                setSkillProgress(binding.progressOverall, stats.overall)
//                setSkillProgress(binding.progressPronounciation, stats.pronunciation)

                // CALENDAR
                val mapped = mapCalendar(calendar)

                val firstDate = calendar.firstOrNull()?.date

                val monthTitle = if (firstDate != null && firstDate.length >= 7) {
                    val year = firstDate.substring(0, 4)
                    val month = firstDate.substring(5, 7)

                    val monthName = when (month) {
                        "01" -> "January"
                        "02" -> "February"
                        "03" -> "March"
                        "04" -> "April"
                        "05" -> "May"
                        "06" -> "June"
                        "07" -> "July"
                        "08" -> "August"
                        "09" -> "September"
                        "10" -> "October"
                        "11" -> "November"
                        "12" -> "December"
                        else -> ""
                    }

                    "$monthName, $year"
                } else {
                    "No data"
                }

                val activeDays = mutableSetOf<LocalDate>()

                calendar.forEach {

                    try {

                        val date = LocalDate.parse(it.date)

                        activeDays.add(date)

                    } catch (_: Exception) {

                    }
                }

                binding.calendarView.setDays(activeDays)

            } catch (e: Exception) {
                Log.e("Statistics", "ERROR = ${e.message}", e)
                binding.tvStreak.text = "No activity yet"
//
//                setSkillCard(binding.tvFluencyValue, 0.0, false)
//                setSkillCard(binding.tvOverallValue, 0.0, false)
//                setSkillCard(binding.tvPronunciationValue, 0.0, false)
//
//                setSkillProgress(binding.progressFluency, 0.0)
//                setSkillProgress(binding.progressOverall, 0.0)
//                setSkillProgress(binding.progressPronounciation, 0.0)
            }
        }
    }

    // -------------------------
    // 🔥 SKILL UI
    // -------------------------
    private fun setSkillCard(
        valueView: TextView,
        value: Double,
        isActive: Boolean
    ) {
        valueView.text = value.toInt().toString()

        if (isActive) {
            valueView.setTextColor(requireContext().getColor(R.color.black))
        } else {
            valueView.setTextColor(requireContext().getColor(R.color.gray))
        }
    }

    private fun setSkillProgress(progressBar: ProgressBar, value: Double) {
        progressBar.progress = value.toInt()
    }

    // -------------------------
    // 🔥 CALENDAR MAPPING
    // -------------------------
    private fun mapCalendar(
        data: List<com.example.tildau.data.model.stats.ActivityDayDto>
    ): List<CalendarDay> {

        val result = mutableListOf<CalendarDay>()

        val currentDate = LocalDate.now()
        val currentMonth = YearMonth.from(currentDate)

        val daysInMonth = currentMonth.lengthOfMonth()

        val firstDayOfMonth = currentDate.withDayOfMonth(1)

// Monday = 1 ... Sunday = 7
        val startOffset = firstDayOfMonth.dayOfWeek.value - 1

        if (data.isEmpty()) {

            repeat(startOffset) {
                result.add(CalendarDay())
            }

            for (i in 1..daysInMonth) {
                result.add(
                    CalendarDay(
                        number = i,
                        isStreak = false
                    )
                )
            }

            while (result.size < 42) {
                result.add(CalendarDay())
            }

            return result
        }

        // backend даты
        val activeDays = mutableSetOf<LocalDate>()

        data.forEach { item ->

            try {

                val date = LocalDate.parse(item.date)

                activeDays.add(date)

            } catch (_: Exception) {

            }
        }

        repeat(startOffset) {
            result.add(CalendarDay())
        }

        for (day in 1..daysInMonth) {

            val currentDayDate = currentMonth.atDay(day)

            result.add(
                CalendarDay(
                    number = day,
                    isStreak = activeDays.contains(currentDayDate)
                )
            )
        }

        while (result.size < 42) {
            result.add(CalendarDay())
        }

        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}