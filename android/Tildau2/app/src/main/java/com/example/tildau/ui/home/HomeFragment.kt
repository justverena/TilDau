package com.example.tildau.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.remote.StatisticsApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.data.repository.StatisticsRepository
import com.example.tildau.databinding.FragmentHomeBinding
import com.example.tildau.ui.achievements.AchievementRegistry
import com.example.tildau.ui.achievements.AchievementType
import com.example.tildau.ui.courses.CourseViewModel
import com.example.tildau.ui.courses.CourseViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeFragment : Fragment() {

    private lateinit var coursesRepository: CourseRepository

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val statisticsRepository by lazy {
        val api = ApiClient.createServiceWithToken(
            StatisticsApi::class.java
        ) {
            TokenManager.getToken(requireContext())
        }
        StatisticsRepository(api)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupCoursesRepository()

        loadCourses()
        loadStats()
        loadCalendar()

        observeAchievements()
        setupNavigation()
    }

    // ---------------- ACHIEVEMENTS ----------------

    private fun observeAchievements() {
        binding.homeAchievementsView.submitList(
            AchievementType.values().toList()
        )
    }

    // ---------------- COURSES ----------------

    private fun setupCoursesRepository() {
        val api = ApiClient.createServiceWithToken(
            CourseApi::class.java
        ) {
            TokenManager.getToken(requireContext())
        }

        coursesRepository = CourseRepository(api)
    }

    private fun loadCourses() {
        lifecycleScope.launch {
            val courses = coursesRepository.getCourses() ?: emptyList()
            binding.yourCoursesView.submitCourses(courses)
        }
    }

    // ---------------- STATS ----------------

    private fun loadStats() {
        lifecycleScope.launch {
            val stats = statisticsRepository.getSkillTrend()

            binding.skillsView.setData(
                fluency = stats.fluency.toInt(),
                pronunciation = stats.pronunciation.toInt(),
                overall = stats.overall.toInt()
            )
        }
    }

    // ---------------- CALENDAR ----------------

    private fun loadCalendar() {
        lifecycleScope.launch {
            val calendar = statisticsRepository.getCalendar()

            val activeDays = calendar.mapNotNull {
                runCatching { LocalDate.parse(it.date) }.getOrNull()
            }.toSet()

            binding.weeklyCalendar.setDays(activeDays)
        }
    }

    // ---------------- NAVIGATION ----------------

    private fun setupNavigation() {
        binding.yourCoursesView.onViewMoreClicked =
            { findNavController().navigate(R.id.coursesFragment) }

        binding.yourCoursesView.onCourseClicked = { course ->
            val bundle = Bundle().apply {
                putString("courseId", course.id)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_courseFragment,
                bundle
            )
        }

        binding.skillsView.setOnClickListener {
            findNavController().navigate(R.id.statisticsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}