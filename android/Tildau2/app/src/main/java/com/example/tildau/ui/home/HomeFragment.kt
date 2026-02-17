package com.example.tildau.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tildau.databinding.FragmentHomeBinding
import com.example.tildau.ui.main.MainActivity
import com.example.tildau.ui.profile.AccountFragment
import com.example.tildau.ui.record.RecordFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            (activity as? MainActivity)?.openFragment(AccountFragment())
        }

        binding.btnOpenRecorder.setOnClickListener {
            (activity as? MainActivity)?.openFragment(RecordFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


