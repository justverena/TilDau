package com.example.tildau.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tildau.databinding.FragmentAchievementBinding

class AchievementFragment : Fragment() {

    private var _binding: FragmentAchievementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentAchievementBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        binding.rvAchievements.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvAchievements.adapter =
            AchievementListAdapter(
                AchievementType.values().toList()
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}