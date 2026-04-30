package com.example.tildau.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.tildau.R
import com.example.tildau.databinding.FragmentStatisticsBinding
import com.example.tildau.ui.achievements.AchievementAdapter
import com.example.tildau.ui.achievements.AchievementType
import com.example.tildau.ui.calendar.CalendarDay
import java.time.LocalDate

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var achievementAdapter: AchievementAdapter
    private val snapHelper = PagerSnapHelper()

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

        initAchievements()
        loadFakeAchievements()
        initSkills()
        initCalendar()
    }

    private fun setSkillCard(
        valueView: TextView,
        titleView: TextView? = null,
        value: Int,
        isActive: Boolean
    ) {
        valueView.text = value.toString()

        if (isActive) {
            valueView.setTextColor(requireContext().getColor(R.color.black))
            titleView?.setTextColor(requireContext().getColor(R.color.black))
        } else {
            valueView.setTextColor(requireContext().getColor(R.color.black))
            titleView?.setTextColor(requireContext().getColor(R.color.gray))
        }
    }

    private fun setSkillProgress(progressBar: ProgressBar, value: Int) {
        progressBar.progress = value
    }

    private fun initAchievements() {
        achievementAdapter = AchievementAdapter()

        binding.rvAchievements.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = achievementAdapter
        }

        if (binding.rvAchievements.onFlingListener == null) {
            snapHelper.attachToRecyclerView(binding.rvAchievements)
        }
    }

    private fun initSkills() {

        // 🔴 ЗАГЛУШКА
        setSkillCard(
            valueView = binding.tvFluencyValue,
            titleView = null,
            value = 8,
            isActive = true
        )

        setSkillCard(
            valueView = binding.tvOverallValue,
            titleView = null,
            value = 75,
            isActive = false
        )

        setSkillCard(
            valueView = binding.tvPronunciationValue,
            titleView = null,
            value = 65,
            isActive = false
        )

        setSkillProgress(binding.progressFluency, 87)
        setSkillProgress(binding.progressOverall, 75)
        setSkillProgress(binding.progressPronounciation, 65)

        // 🔵 РЕАЛЬНАЯ ЛОГИКА (С БЭКА)
        /*
        viewModel.skills.observe(viewLifecycleOwner) { stats ->
            setSkillCard(binding.tvFluencyValue, null, stats.fluency, true)
            setSkillCard(binding.tvOverallValue, null, stats.overall, false)
            setSkillCard(binding.tvPronunciationValue, null, stats.pronunciation, false)
        }
        */
    }

    private fun initCalendar() {

        val days = mutableListOf<CalendarDay>()

        // пустые дни в начале (пример: неделя начинается со среды)
        repeat(2) {
            days.add(CalendarDay())
        }

        // 1 неделя (где есть streak)
        for (i in 1..7) {

            val isStreak =
                (i == 3) ||  // первая среда (например)
                        (i == 5) ||  // "2-й день после среды"
                        (i == 6)     // суббота

            days.add(CalendarDay(
                number = i,
                isStreak = isStreak
            ))
        }

        // 2 неделя и дальше — БЕЗ streak
        for (i in 8..30) {
            days.add(CalendarDay(
                number = i,
                isStreak = false
            ))
        }

        // добивка до 42
        while (days.size < 42) {
            days.add(CalendarDay())
        }

        binding.calendarView.setMonthTitle("April, 2026")
        binding.calendarView.setDays(days)
    }

    private fun loadFakeAchievements() {
        val data = AchievementType.values().toList()
        achievementAdapter.submitList(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}