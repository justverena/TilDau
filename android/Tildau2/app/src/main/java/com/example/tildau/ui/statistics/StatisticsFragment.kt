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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.ui.achievements.AchievementAdapter

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val achievementAdapter = AchievementAdapter()
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

        binding.rvAchievements.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvAchievements.adapter = achievementAdapter

        loadStatisticsFromBackend()
    }
    private fun loadStatisticsFromBackend() {
        lifecycleScope.launch {
            try {
                val streak = repository.getStreak()
                val stats = repository.getSkillTrend()

                Log.d("STATS", "fluency=${stats.fluency}")
                Log.d("STATS", "overall=${stats.overall}")
                Log.d("STATS", "pronunciation=${stats.pronunciation}")
                val calendar = repository.getCalendar()
                val achievements = repository.getAchievements()

                // STREAK
                binding.tvStreak.text =
                    getString(R.string.practiced_days_in_row, streak)

//                // SKILLS
                binding.cardFluency.setData(
                    title = getString(R.string.fluency),
                    value = stats.fluency.toInt(),
                    isActive = true
                )

                binding.cardOverall.setData(
                    title = getString(R.string.overall),
                    value = stats.overall.toInt(),
                    isActive = false
                )

                binding.cardPronunciation.setData(
                    title = getString(R.string.pronunciation),
                    value = stats.pronunciation.toInt(),
                    isActive = false
                )

                binding.cardFluency.setCardBackgroundResource(
                    R.drawable.bg_stat_card
                )

                binding.cardPronunciation.setCardBackgroundResource(
                    R.drawable.bg_stat_card
                )

                binding.cardOverall.setCardBackgroundResource(
                    R.drawable.bg_stat_card_center
                )

                val activeDays = mutableSetOf<LocalDate>()

                calendar.forEach {

                    try {

                        val date = LocalDate.parse(it.date)

                        activeDays.add(date)

                    } catch (_: Exception) {

                    }
                }

                binding.calendarView.setDays(activeDays)
                achievementAdapter.submitList(achievements)

            } catch (e: Exception) {
                Log.e("Statistics", "ERROR = ${e.message}", e)
                binding.tvStreak.text = "Әзірге белсенділік жоқ"
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}