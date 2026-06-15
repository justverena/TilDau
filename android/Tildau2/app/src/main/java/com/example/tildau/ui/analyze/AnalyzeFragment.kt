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
import com.example.tildau.navigation.CourseFlowCoordinator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import android.util.Log

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyzeViewModel by viewModels()

    private var audioPath: String = ""
    private var exerciseId: String = ""

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
            Toast.makeText(requireContext(), "Жаттығу немесе аудиожазба табылмады", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        binding.loadingView.setTitle("Сөзіңіз талданып жатыр...")
        binding.loadingView.setSubtitle("Бұл біраз уақыт алуы мүмкін.")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!isLoading) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        observeState()

        val file = File(audioPath)
        if (!file.exists()) {
            Toast.makeText(requireContext(), "Аудиожазба табылмады", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        viewModel.analyze(audioPath, exerciseId, requireActivity().applicationContext)
    }

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
                            putStringArrayList(
                                "feedback",
                                ArrayList(state.result.feedback)
                            )
                            putSerializable("nextStep", state.result.nextStep)
                            putSerializable(
                                "achievements",
                                ArrayList(state.result.newAchievements)
                            )
                        }

                        val navController = findNavController()
                        val coordinator = CourseFlowCoordinator(navController)

                        coordinator.openResult(bundle)


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