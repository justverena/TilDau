package com.example.tildau.ui.unit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.data.model.course.ExerciseResponse
import com.example.tildau.data.model.course.UnitResponse
import com.example.tildau.databinding.FragmentUnitBinding
import com.example.tildau.ui.exercise.ExercisesAdapter

class UnitFragment : Fragment() {

    private var _binding: FragmentUnitBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExercisesAdapter
    private lateinit var unit: UnitResponse

    companion object {
        private const val ARG_UNIT = "ARG_UNIT"

        fun newInstance(unit: UnitResponse): UnitFragment {
            val fragment = UnitFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_UNIT, unit)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = arguments?.getSerializable(ARG_UNIT) as UnitResponse
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ExercisesAdapter(unit.exercises)
        binding.recyclerViewExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExercises.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
