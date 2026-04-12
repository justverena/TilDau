package com.example.tildau.ui.courses

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.next.NextStepType
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.databinding.FragmentCoursesBinding
import androidx.navigation.fragment.findNavController
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.navigation.NextStepHandler
import com.example.tildau.ui.onboarding.DefectOnboardingActivity
import kotlinx.coroutines.launch

class CoursesFragment : Fragment() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var courseAdapter: CoursesAdapter
    private lateinit var repository: CourseRepository
    private lateinit var authRepository: AuthRepository

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRepository()
        setupRecycler()
        setupViewModel()
        observeViewModel()

        viewModel.loadCourses()
    }

    private fun setupRepository() {
        val courseApi = ApiClient.createServiceWithToken(
            CourseApi::class.java
        ) { TokenManager.getToken(requireContext()) }

        repository = CourseRepository(courseApi)


        val authApi = ApiClient.createServiceWithToken(
            AuthApi::class.java
        ) { TokenManager.getToken(requireContext()) }

        authRepository = AuthRepository(authApi)
    }

    private fun setupRecycler() {
        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(requireContext())

        courseAdapter = CoursesAdapter(emptyList()) { course ->
            onCourseClicked(course.id)
        }

        binding.recyclerViewCourses.adapter = courseAdapter
    }

    private fun setupViewModel() {
        val factory = CourseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.courses.observe(viewLifecycleOwner) {
            courseAdapter.updateCourses(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔥 ГЛАВНАЯ ЛОГИКА
    private fun onCourseClicked(courseId: String) {
        val bundle = Bundle().apply {
            putString("courseId", courseId)
        }

        findNavController().navigate(
            R.id.action_coursesFragment_to_courseFragment,
            bundle
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}