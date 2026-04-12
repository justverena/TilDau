package com.example.tildau.ui.record

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.exercise.ExerciseFullResponse
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.ExerciseApi
import com.example.tildau.databinding.FragmentRecordBinding
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseId: String
    private var fullExercise: ExerciseFullResponse? = null

    private lateinit var recorder: WavAudioRecorder
    private lateinit var audioPlayer: AudioPlayerView

    private enum class RecordingState { IDLE, RECORDING, FINISHED }
    private var currentState = RecordingState.IDLE

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startRecording()
            else Toast.makeText(requireContext(), "Microphone permission required", Toast.LENGTH_SHORT).show()
        }

    companion object {
        private const val ARG_EXERCISE_ID = "ARG_EXERCISE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseId = arguments?.getString(ARG_EXERCISE_ID) ?: ""
        if (exerciseId.isEmpty()) {
            Toast.makeText(requireContext(), "Exercise ID missing", Toast.LENGTH_SHORT).show()
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

        recorder = WavAudioRecorder(requireContext())
        audioPlayer = binding.audioPlayer

        updateUI(RecordingState.IDLE)
        fetchFullExercise()
        setupRecording()
    }

    private fun updateUI(state: RecordingState) {
        currentState = state

        when (state) {
            RecordingState.IDLE -> {
                binding.btnRecord.setImageResource(R.drawable.btn_record)
                binding.btnSecondaryLeft.visibility = View.GONE
                binding.btnSecondaryRight.visibility = View.GONE
                binding.recordHint.text = "Tap to start recording"
            }
            RecordingState.RECORDING -> {
                binding.btnRecord.setImageResource(R.drawable.btn_pause)
                binding.btnSecondaryLeft.visibility = View.GONE
                binding.btnSecondaryRight.visibility = View.GONE
                binding.recordHint.text = "Recording..."
            }
            RecordingState.FINISHED -> {
                binding.btnRecord.setImageResource(R.drawable.btn_play2)
                binding.btnSecondaryLeft.visibility = View.VISIBLE
                binding.btnSecondaryRight.visibility = View.VISIBLE
                binding.btnSecondaryLeft.setImageResource(R.drawable.btn_replay)
                binding.btnSecondaryRight.setImageResource(R.drawable.btn_next)
                binding.recordHint.text = "Listen or continue"
            }
        }
    }

    private fun fetchFullExercise() {
        val api = ApiClient.createServiceWithToken(ExerciseApi::class.java) {
            TokenManager.getToken(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exercise = api.getExercise(exerciseId)
                fullExercise = exercise

                bindExerciseUI(exercise)

                setupExerciseProgress()
            } catch (e: Exception) {
                Log.e("RecordFragment", "Failed to load exercise", e)
                Toast.makeText(requireContext(), "Failed to load exercise", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupExerciseProgress() {
        val currentExercise = arguments?.getInt("currentExerciseNumber") ?: 1
        val totalExercises = arguments?.getInt("totalExercises") ?: 3

        binding.exerciseProgress.setProgressTextView(binding.exerciseProgressText)
        binding.exerciseProgress.setup(totalExercises)
        binding.exerciseProgress.update(currentExercise - 1)
    }

    private fun setupRecording() {

        binding.btnRecord.setOnClickListener {
            when (currentState) {
                RecordingState.IDLE -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                RecordingState.RECORDING -> {
                    stopRecording()
                    updateUI(RecordingState.FINISHED)
                }
                RecordingState.FINISHED -> playRecordedAudio()
            }
        }

        binding.btnSecondaryLeft.setOnClickListener {
            cancelRecording()
            updateUI(RecordingState.IDLE)
        }

        binding.btnSecondaryRight.setOnClickListener {
            stopRecordingAndNavigate()
        }

        recorder.onAmplitudeChanged = { amplitude ->
            requireActivity().runOnUiThread {
                binding.waveformView.addAmplitude(amplitude)
            }
        }
    }

    private fun startRecording() {
        recorder.startRecording()
        updateUI(RecordingState.RECORDING)
    }

    private fun stopRecording() {
        recorder.stopRecording()
    }

    private fun cancelRecording() {
        recorder.stopRecording()
        binding.waveformView.clear()
    }

    private fun playRecordedAudio() {
        val path = recorder.outputFilePath ?: return

        binding.audioContainer.visibility = View.VISIBLE

        audioPlayer.release()
        audioPlayer.setAudio(path)
        audioPlayer.play()
    }

    private fun stopRecordingAndNavigate() {
        val audioPath = recorder.outputFilePath ?: return

        val bundle = Bundle().apply {
            putString("audioPath", audioPath)
            putString("exerciseId", exerciseId)
            putString("title", fullExercise?.title)
            putString("subtitle", fullExercise?.instruction)
        }

        findNavController().navigate(R.id.action_recordFragment_to_analyzeFragment, bundle)
    }

    private fun bindExerciseUI(exercise: ExerciseFullResponse) {

        binding.exerciseTitle.text = exercise.title
        binding.exerciseInstructionText.text = exercise.instruction

        binding.expectedText.text =
            if (!exercise.expectedText.isNullOrEmpty())
                exercise.expectedText
            else
                "Listen to the audio"

        val hasAudio = !exercise.referenceAudioUrl.isNullOrEmpty()

        if (hasAudio) {
            binding.audioContainer.visibility = View.VISIBLE
            binding.audioPlayer.setAudio(exercise.referenceAudioUrl!!)
        } else {
            binding.audioContainer.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recorder.stopRecording()
        audioPlayer.release()
        _binding = null
    }
}