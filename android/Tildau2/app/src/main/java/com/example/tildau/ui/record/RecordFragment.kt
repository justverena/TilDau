package com.example.tildau.ui.record

import android.Manifest
import android.media.AudioAttributes
import android.media.MediaPlayer
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
import java.io.IOException

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseId: String
    private var fullExercise: ExerciseFullResponse? = null

    private lateinit var recorder: WavAudioRecorder
    private var isRecording = false
    private var mediaPlayer: MediaPlayer? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startRecording()
            else Toast.makeText(requireContext(), "Microphone permission required", Toast.LENGTH_SHORT).show()
        }

    companion object {
        private const val ARG_EXERCISE_ID = "ARG_EXERCISE_ID"

        fun newInstance(exerciseId: String): RecordFragment {
            val fragment = RecordFragment()
            val bundle = Bundle()
            bundle.putString(ARG_EXERCISE_ID, exerciseId)
            fragment.arguments = bundle
            return fragment
        }
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

        fetchFullExercise()
        setupRecording()
    }

    private fun fetchFullExercise() {
        val api = ApiClient.createServiceWithToken(ExerciseApi::class.java) {
            TokenManager.getToken(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exercise = api.getExercise(exerciseId)
                fullExercise = exercise

                binding.exerciseTitle.text = exercise.title
                binding.exerciseInstructionText.text = exercise.instruction

                val hasAudio = !exercise.referenceAudioUrl.isNullOrEmpty()
                val hasText = !exercise.expectedText.isNullOrEmpty()

// 🔹 Если есть текст упражнения
                if (hasText) {
                    binding.expectedText.visibility = View.VISIBLE
                    binding.expectedText.text = exercise.expectedText
                } else {
                    // Если текста нет, но есть аудио — показываем подсказку
                    binding.expectedText.visibility = View.VISIBLE
                    binding.expectedText.text = "Listen to the audio"
                }

// 🔹 Если есть аудио — показываем кнопку
                if (hasAudio) {
                    binding.btnListen.visibility = View.VISIBLE
                    setupListenButton()
                } else {
                    // Если аудио нет — полностью скрываем кнопку
                    binding.btnListen.visibility = View.GONE
                }


            } catch (e: Exception) {
                Log.e("RecordFragment", "Failed to load exercise", e)
                Toast.makeText(requireContext(), "Failed to load exercise", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListenButton() {
        binding.btnListen.setOnClickListener {
            val url = fullExercise?.referenceAudioUrl
            if (url.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No reference audio available", Toast.LENGTH_SHORT).show()
                Log.e("RecordFragment", "Reference audio URL is null or empty")
                return@setOnClickListener
            }
            playAudioFromUrl(url)
        }
    }

    private fun setupRecording() {
        binding.btnRecord.setOnClickListener {
            if (!isRecording) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            else stopRecordingAndNavigate()
        }

        binding.btnStop.setOnClickListener {
            if (isRecording) cancelRecording()
        }

        binding.btnPlayRecord.setOnClickListener {
            playRecordedAudio()
        }

        recorder.onAmplitudeChanged = { amplitude ->
            requireActivity().runOnUiThread {
                binding.waveformView.addAmplitude(amplitude)
            }
        }
    }

    private fun startRecording() {
        recorder.startRecording()
        isRecording = true
    }

    private fun stopRecordingAndNavigate() {
        recorder.stopRecording()
        isRecording = false

        val audioPath = recorder.outputFilePath
        if (audioPath.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Audio recording failed", Toast.LENGTH_SHORT).show()
            return
        }

        // Передаем данные в AnalyzeFragment через NavController
        val bundle = Bundle().apply {
            putString("audioPath", audioPath)
            putString("exerciseId", exerciseId)
            putString("title", fullExercise?.title)
            putString("subtitle", fullExercise?.instruction)
        }

        findNavController().navigate(R.id.action_recordFragment_to_analyzeFragment, bundle)
    }

    private fun cancelRecording() {
        recorder.stopRecording()
        isRecording = false
        binding.waveformView.clear()
    }

    private fun playRecordedAudio() {
        val path = recorder.outputFilePath ?: return
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(path)
            prepare()
            start()
        }
    }

    private fun playAudioFromUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { start() }
                setOnCompletionListener { Log.d("RecordFragment", "Playback completed") }
                setOnErrorListener { _, what, extra ->
                    Log.e("RecordFragment", "MediaPlayer error: what=$what extra=$extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: IOException) {
            Log.e("RecordFragment", "Audio playback failed", e)
            Toast.makeText(requireContext(), "Audio playback failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isRecording) recorder.stopRecording()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}