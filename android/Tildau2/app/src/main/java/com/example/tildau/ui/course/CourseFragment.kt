package com.example.tildau.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.R
import com.example.tildau.data.enums.UnitState
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.databinding.FragmentCourseBinding
import com.example.tildau.ui.courses.CourseViewModel
import com.example.tildau.ui.courses.CourseViewModelFactory
import com.example.tildau.navigation.NextStepHandler
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ARG_COURSE_ID = "courseId"
        fun newInstance(courseId: String) = CourseFragment().apply {
            arguments = Bundle().apply { putString(ARG_COURSE_ID, courseId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCourseBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CourseAdapter(mutableListOf())
        binding.courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.courseRecyclerView.adapter = adapter

        val courseId = arguments?.getString(ARG_COURSE_ID)
        if (courseId == null) { Log.d("CourseFragment", "courseId is null!"); return }

        saveCurrentCourseId(courseId)
//        setupStartButton(courseId)

        val courseApi = ApiClient.createServiceWithToken(CourseApi::class.java) { TokenManager.getToken(requireContext()) }
        val repository = CourseRepository(courseApi)
        viewModel = ViewModelProvider(this, CourseViewModelFactory(repository))[CourseViewModel::class.java]

        observeViewModel()
        lifecycleScope.launch {
            try {
                val authApi = ApiClient.createServiceWithToken(
                    AuthApi::class.java
                ) { TokenManager.getToken(requireContext()) }

                val authRepository = AuthRepository(authApi)

                val hasDefects = authRepository.checkDefects()
                if (!hasDefects) {
                    Toast.makeText(requireContext(), "No access", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                viewModel.loadCourseById(courseId)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedCourse.observe(viewLifecycleOwner) { course ->
            android.util.Log.d("COURSE_DEBUG", "COURSE = $course")

            course?.let {
                android.util.Log.d("COURSE_DEBUG", "UNITS SIZE = ${it.units.size}")
                adapter.updateItems(buildCourseItems(it))
            }
        }
    }

//    private fun setupStartButton(courseId: String) {
//        binding.btnStartResume.setOnClickListener {
//            lifecycleScope.launch {
//                try {
//                    val courseApi = ApiClient.createServiceWithToken(CourseApi::class.java) { TokenManager.getToken(requireContext()) }
//                    val repository = CourseRepository(courseApi)
//                    val nextStep = try { repository.resumeCourse(courseId) } catch (e: Exception) { null }
//                    val finalStep = nextStep ?: repository.startCourse(courseId)
//                    if (finalStep != null) NextStepHandler.handle(this@CourseFragment, finalStep)
//                    else Toast.makeText(requireContext(), "Failed to start", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun saveCurrentCourseId(courseId: String) =
        requireContext().getSharedPreferences("app_prefs", 0).edit().putString("current_course_id", courseId).apply()

    private fun buildCourseItems(course: CourseFullResponse): MutableList<CourseItem> {
        val items = mutableListOf<CourseItem>()

        val nextExercise = course.units
            .flatMap { it.exercises }
            .firstOrNull { !it.isCompleted && !it.isLocked }

        items.add(CourseItem.CourseCard)
        items.add(CourseItem.Header(course.title, course.description))
        val overallProgress = course.progressPercent.toInt()
        val resumeText = if (nextExercise != null) {
            "Continue: ${nextExercise.title}"
        } else {
            "🎉 Course completed"
        }

        items.add(CourseItem.ProgressBox(overallProgress, resumeText))

        items.add(CourseItem.InfoRow(sections = course.units.size, hours = "10 hours"))


        course.units.forEachIndexed { index, unit ->
            val state = when {
                unit.isCompleted -> UnitState.COMPLETED
                unit.exercises.any { !it.isLocked } -> UnitState.CURRENT
                else -> UnitState.LOCKED
            }
            val totalExercises = unit.exercises.size
            val completedExercises = unit.exercises.count { it.isCompleted }
            val progressPercent = if (totalExercises > 0) completedExercises * 100 / totalExercises else 0

            items.add(CourseItem.Unit(index + 1, unit.title, unit.description, unit, state, progressPercent))
        }

        return items
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}