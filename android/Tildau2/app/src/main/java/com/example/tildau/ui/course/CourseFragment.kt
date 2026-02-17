package com.example.tildau.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.course.CourseFullResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.databinding.FragmentCourseBinding
import com.example.tildau.ui.courses.CourseViewModel
import com.example.tildau.ui.courses.CourseViewModelFactory
import com.example.tildau.ui.unit.UnitFragment

class CourseFragment : Fragment() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter
    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_COURSE_ID = "COURSE_ID"

        fun newInstance(courseId: String): CourseFragment {
            val fragment = CourseFragment()
            val args = Bundle()
            args.putString(ARG_COURSE_ID, courseId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CourseAdapter(emptyList()) { unitResponse ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UnitFragment.newInstance(unitResponse))
                .addToBackStack(null)
                .commit()
        }


        binding.courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.courseRecyclerView.adapter = adapter

        val courseId = arguments?.getString(ARG_COURSE_ID) ?: return

        val courseApi = ApiClient.createServiceWithToken(
            CourseApi::class.java
        ) { TokenManager.getToken(requireContext()) }

        val repository = CourseRepository(courseApi)
        val factory = CourseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]

        observeViewModel()
        viewModel.loadCourseById(courseId)
    }

    private fun observeViewModel() {
        viewModel.selectedCourse.observe(viewLifecycleOwner) { course ->
            course?.let {
                adapter.updateItems(buildCourseItems(it))
            }
        }
    }

    private fun buildCourseItems(course: CourseFullResponse): List<CourseItem> {
        val items = mutableListOf<CourseItem>()
        items.add(CourseItem.Header(course.title, course.description))

        course.units.forEachIndexed { index, unit ->
            items.add(
                CourseItem.Unit(
                    number = index + 1,
                    title = unit.title,
                    description = unit.description,
                    unitResponse = unit
                )
            )
        }
        return items
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
