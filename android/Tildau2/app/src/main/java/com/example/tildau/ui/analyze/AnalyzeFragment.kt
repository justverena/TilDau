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

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyzeViewModel by viewModels()

    private var audioPath: String = ""
    private var exerciseId: String = ""
    private var title: String? = null
    private var subtitle: String? = null

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
        // Получаем аргументы
        title = arguments?.getString("title")
        subtitle = arguments?.getString("subtitle")
        audioPath = arguments?.getString("audioPath") ?: ""
        exerciseId = arguments?.getString("exerciseId") ?: ""

        if (audioPath.isEmpty() || exerciseId.isEmpty()) {
            Toast.makeText(requireContext(), "Audio file or exercise missing", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        // Настраиваем LoadingView
        binding.loadingView.setTitle(title)
        binding.loadingView.setSubtitle(subtitle)

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