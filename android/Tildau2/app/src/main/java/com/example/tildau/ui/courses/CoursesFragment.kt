package com.example.tildau.ui.courses

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.data.local.TokenManager
import com.example.tildau.databinding.FragmentCoursesBinding
import com.example.tildau.ui.course.CourseFragment

class CoursesFragment : Fragment() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var courseAdapter: CoursesAdapter
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

        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(requireContext())
        courseAdapter = CoursesAdapter(emptyList()) { course ->

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CourseFragment.newInstance(course.id))
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerViewCourses.adapter = courseAdapter

        val courseApi = ApiClient.createServiceWithToken(
            CourseApi::class.java
        ) { TokenManager.getToken(requireContext()) }

        val repository = CourseRepository(courseApi)
        val factory = CourseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]

        observeViewModel()
        viewModel.loadCourses()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            courseAdapter.updateCourses(courses)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
