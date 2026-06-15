package com.example.tildau.ui.record

import android.Manifest
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.databinding.FragmentRecordBinding
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecordViewModel by viewModels()

    private lateinit var exerciseId: String
    private lateinit var recordManager: RecordManager
    private lateinit var audioPlayer: AudioPlayerView

    private var recordedAudioPlayer: MediaPlayer? = null




    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startRecording()
            else Toast.makeText(
                requireContext(),
                "Микрофонға рұқсат қажет",
                Toast.LENGTH_SHORT
            ).show()
        }

    companion object {
        private const val ARG_EXERCISE_ID = "ARG_EXERCISE_ID"
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exerciseId = arguments?.getString(ARG_EXERCISE_ID) ?: ""

        if (exerciseId.isEmpty()) {
            Toast.makeText(requireContext(), "Жаттығу идентификаторы табылмады", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupManagers()
        setupUI()
        observeViewModel()

        viewModel.loadExercise(exerciseId)
    }




    private fun setupManagers() {
        recordManager = RecordManager(WavAudioRecorder(requireContext()))
        audioPlayer = binding.audioPlayer

        recordManager.onAmplitudeChanged = { amplitude ->
            requireActivity().runOnUiThread {
                binding.waveformView.addAmplitude(amplitude)
            }
        }

    }

    private fun setupUI() {

        binding.btnRecord.setOnClickListener {
            when (viewModel.uiState.value) {

                RecordViewModel.RecordingState.IDLE -> {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                RecordViewModel.RecordingState.RECORDING -> {
                    recordManager.stopRecording()
                    viewModel.setState(RecordViewModel.RecordingState.FINISHED)
                }

                RecordViewModel.RecordingState.FINISHED -> {
                    playRecordedAudio()
                }

                RecordViewModel.RecordingState.PLAYING -> {
                    stopPlayback()
                    viewModel.setState(RecordViewModel.RecordingState.FINISHED)
                }
            }
        }

        binding.btnSecondaryLeft.setOnClickListener {
            cancelRecording()
        }

        binding.btnSecondaryRight.setOnClickListener {
            stopRecordingAndNavigate()
        }
    }




    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }

        lifecycleScope.launch {
            viewModel.exercise.collect { exercise ->
                exercise?.let { bindExerciseUI(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun updateUI(state: RecordViewModel.RecordingState) {

        when (state) {

            RecordViewModel.RecordingState.IDLE -> {
                binding.btnRecord.setImageResource(R.drawable.btn_record)
                binding.btnSecondaryLeft.visibility = View.GONE
                binding.btnSecondaryRight.visibility = View.GONE
                binding.recordHint.text = "Жазуды бастау үшін басыңыз"
            }

            RecordViewModel.RecordingState.RECORDING -> {
                binding.btnRecord.setImageResource(R.drawable.btn_pause)
                binding.btnSecondaryLeft.visibility = View.GONE
                binding.btnSecondaryRight.visibility = View.GONE
                binding.recordHint.text = "Жазылуда..."
            }

            RecordViewModel.RecordingState.FINISHED -> {
                binding.btnRecord.setImageResource(R.drawable.btn_play2)
                binding.btnSecondaryLeft.visibility = View.VISIBLE
                binding.btnSecondaryRight.visibility = View.VISIBLE
                binding.recordHint.text = "Жазбаны тыңдау үшін басыңыз"
            }

            RecordViewModel.RecordingState.PLAYING -> {
                binding.btnRecord.setImageResource(R.drawable.btn_pause)
                binding.btnSecondaryLeft.visibility = View.VISIBLE
                binding.btnSecondaryRight.visibility = View.VISIBLE
                binding.recordHint.text = "Ойнатылуда..."
            }
        }
    }




    private fun startRecording() {
        recordManager.startRecording()
        viewModel.setState(RecordViewModel.RecordingState.RECORDING)
    }

    private fun cancelRecording() {
        recordManager.cancelRecording()
        binding.waveformView.clear()
        viewModel.setState(RecordViewModel.RecordingState.IDLE)
    }




    private fun playRecordedAudio() {

        val path = recordManager.outputPath ?: return

        stopRecordedAudioPlayback()

        recordedAudioPlayer = MediaPlayer().apply {

            setDataSource(path)

            setOnPreparedListener {
                start()

                viewModel.setState(RecordViewModel.RecordingState.PLAYING)
            }

            setOnCompletionListener {

                viewModel.setState(RecordViewModel.RecordingState.FINISHED)

                release()
                recordedAudioPlayer = null
            }

            prepareAsync()
        }
    }

    private fun stopPlayback() {

        stopRecordedAudioPlayback()

        viewModel.setState(RecordViewModel.RecordingState.FINISHED)
    }

    private fun stopRecordedAudioPlayback() {

        recordedAudioPlayer?.apply {

            if (isPlaying) {
                stop()
            }

            release()
        }

        recordedAudioPlayer = null
    }




    private fun stopRecordingAndNavigate() {
        val audioPath = recordManager.outputPath ?: return
        val exercise = viewModel.exercise.value

        val bundle = Bundle().apply {
            putString("audioPath", audioPath)
            putString("exerciseId", exerciseId)
            putString("title", exercise?.title)
            putString("subtitle", exercise?.instruction)
        }

        findNavController().navigate(
            R.id.action_recordFragment_to_analyzeFragment,
            bundle
        )
    }




    private fun bindExerciseUI(exercise: com.example.tildau.data.model.exercise.ExerciseFullResponse) {

        binding.exerciseTitle.text = exercise.title
        binding.exerciseInstructionText.text = exercise.instruction

        binding.expectedText.text =
            exercise.expectedText?.takeIf { it.isNotEmpty() }
                ?: "Аудионы тыңдаңыз"

        val audioUrl = exercise.referenceAudioUrl?.let {
            "http://10.0.2.2$it"
        }
        Log.d("AUDIO_DEBUG", "FINAL PLAY URL = $audioUrl")

        if (!audioUrl.isNullOrEmpty()) {
            binding.audioContainer.visibility = View.VISIBLE
            binding.audioPlayer.setAudio(audioUrl)
        } else {
            binding.audioContainer.visibility = View.GONE
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()

        recordManager.stopRecording()

        stopRecordedAudioPlayback()

        audioPlayer.release()

        _binding = null
    }
}