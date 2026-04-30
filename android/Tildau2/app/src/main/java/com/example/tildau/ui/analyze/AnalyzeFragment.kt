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
import com.example.tildau.R
import com.example.tildau.databinding.FragmentAnalyzeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import android.util.Log
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.next.NextStepType

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyzeViewModel by viewModels()

    private var audioPath: String = ""
    private var exerciseId: String = ""

    // флаг для защиты back во время загрузки
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        audioPath = arguments?.getString("audioPath") ?: ""
        exerciseId = arguments?.getString("exerciseId") ?: ""

        if (audioPath.isEmpty() || exerciseId.isEmpty()) {
            Toast.makeText(requireContext(), "Audio file or exercise missing", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        binding.loadingView.setTitle("Analyzing your speech...")
        binding.loadingView.setSubtitle("This may take a few minutes.")

        // Toolbar back button
        binding.toolbar.setNavigationOnClickListener {
            if (!isLoading) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // Системная кнопка back
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!isLoading) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // Подписка на состояние ViewModel
        observeState()
//        observeNextStep()


        // Проверяем, что файл существует
        val file = File(audioPath)
        if (!file.exists()) {
            Toast.makeText(requireContext(), "Audio file not found", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        // Запускаем анализ
        viewModel.analyze(audioPath, exerciseId, requireActivity().applicationContext)
    }

//    private fun handleNextStep(nextStep: NextStepResponse) {
//        when (nextStep.type) {
//            NextStepType.EXERCISE, NextStepType.RETRY -> {
//                nextStep.id?.let { id ->
//                    val bundle = Bundle().apply {
//                        putString("ARG_EXERCISE_ID", id)
//                    }
//                    findNavController().navigate(
//                        R.id.action_analyzeFragment_to_recordFragment,
//                        bundle
//                    )
//                }
//            }
//            NextStepType.FINISH -> {
//                Toast.makeText(requireContext(), "Course completed 🎉", Toast.LENGTH_LONG).show()
//                findNavController().navigate(R.id.action_analyzeFragment_to_coursesFragment)
//            }
//            NextStepType.RESOURCE -> {
//                Toast.makeText(requireContext(), "Resource step (not implemented)", Toast.LENGTH_SHORT).show()
//            }
//            else -> {
//                Toast.makeText(requireContext(), "Unknown step type", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    private fun observeNextStep() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.nextStep.collectLatest { nextStep ->
//                nextStep?.let { handleNextStep(it) }
//            }
//        }
//    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AnalyzeState.Loading -> {
                        isLoading = true
                        binding.loadingView.start()
                    }

                    is AnalyzeState.Success -> {
                        isLoading = false
                        binding.loadingView.stop()

                        val bundle = Bundle().apply {
                            putInt("score", state.result.overallScore)
                            putStringArrayList("feedback", ArrayList(state.result.feedback))

                            putSerializable("nextStep", state.result.nextStep)
                        }

                        try {
                            val navController = findNavController()
                            if (navController.currentDestination?.id == R.id.analyzeFragment) {
                                navController.navigate(
                                    R.id.action_analyzeFragment_to_resultFragment,
                                    bundle
                                )
                            }
                        } catch (e: IllegalArgumentException) {
                            Log.w("AnalyzeFragment", "Navigation failed: $e")
                        }
                    }

                    is AnalyzeState.Error -> {
                        isLoading = false
                        binding.loadingView.stop()
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}