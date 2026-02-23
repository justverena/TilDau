package com.example.tildau.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tildau.databinding.FragmentAnalyzeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyzeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val title = arguments?.getString("title")
        val subtitle = arguments?.getString("subtitle")
        val audioPath = arguments?.getString("audioPath") ?: ""

        binding.loadingView.setTitle(title)
        binding.loadingView.setSubtitle(subtitle)

        binding.toolbar.setNavigationOnClickListener {
            // TODO: handle back navigation logic
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // TODO: handle system back press
        }

        observeState()

        viewModel.analyze(audioPath)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AnalyzeState.Loading -> {
                        binding.loadingView.start()
                    }

                    is AnalyzeState.Success -> {
                        findNavController().navigate(
                            // TODO: replace with your real action id
                            com.example.tildau.R.id.action_analyzeFragment_to_resultFragment
                        )
                    }

                    is AnalyzeState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}