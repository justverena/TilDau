package com.example.tildau.ui.unit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.course.UnitResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.ExerciseApi
import com.example.tildau.databinding.FragmentUnitBinding
import com.example.tildau.ui.exercise.ExercisesAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController

class UnitFragment : Fragment() {

    private var _binding: FragmentUnitBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExercisesAdapter
    private lateinit var unit: UnitResponse

    private lateinit var exerciseApi: ExerciseApi

    companion object {
        const val ARG_UNIT = "ARG_UNIT"

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

        exerciseApi = ApiClient.createServiceWithToken(
            ExerciseApi::class.java
        ) { TokenManager.getToken(requireContext()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ExercisesAdapter(unit.exercises) { exercise ->
            adapter = ExercisesAdapter(unit.exercises) { exercise ->
                if (!exercise.isLocked) {
                    fetchFullExerciseAndOpenRecord(exercise.id)
                }
            }
        }

        binding.recyclerViewExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExercises.adapter = adapter
    }

    private fun fetchFullExerciseAndOpenRecord(exerciseId: String) {
        val bundle = bundleOf("ARG_EXERCISE_ID" to exerciseId)
        findNavController().navigate(R.id.action_unitFragment_to_recordFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
