package com.example.tildau.ui.record

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.tildau.databinding.FragmentRecordBinding
import java.io.File

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var recorder: WavAudioRecorder
    private var isRecording = false

    private enum class State {
        IDLE, RECORD, LISTEN
    }

    private var currentState = State.IDLE

    private var mediaPlayer: MediaPlayer? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startRecording()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Microphone permission required",
                    Toast.LENGTH_SHORT
                ).show()
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

        recorder = WavAudioRecorder(requireContext())

        setState(State.IDLE)

        binding.btnRecord.setOnClickListener {
            if (currentState == State.IDLE) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else if (currentState == State.RECORD) {
                stopRecording()
            }
        }

        binding.btnStop.setOnClickListener {
            if (currentState == State.RECORD) {
                cancelRecording()
            }
        }

        binding.btnPlayRecord.setOnClickListener {
            if (currentState == State.RECORD || currentState == State.LISTEN) {
                playRecordedAudio()
            }
        }

        recorder.onAmplitudeChanged = { amplitude ->
            requireActivity().runOnUiThread {
                binding.waveformView.addAmplitude(amplitude)
            }
        }
    }

    private fun setState(state: State) {
        currentState = state
        when (state) {
            State.IDLE -> {
                binding.btnStop.visibility = View.GONE
                binding.btnPlayRecord.visibility = View.GONE
                binding.waveformView.clear()
                binding.btnRecord.background.setTint(Color.parseColor("#6200EE"))
            }
            State.RECORD -> {
                binding.btnStop.visibility = View.VISIBLE
                binding.btnPlayRecord.visibility = View.VISIBLE
                binding.btnRecord.background.setTint(Color.GRAY)
            }
            State.LISTEN -> {
                binding.btnStop.visibility = View.GONE
                binding.btnPlayRecord.visibility = View.VISIBLE
                binding.btnRecord.background.setTint(Color.parseColor("#6200EE"))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        try {
            recorder.startRecording()
            isRecording = true
            setState(State.RECORD)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        recorder.stopRecording()
        isRecording = false
        setState(State.LISTEN)

        Toast.makeText(
            requireContext(),
            "Saved: ${recorder.outputFilePath}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun cancelRecording() {
        if (isRecording) {
            recorder.stopRecording()
            isRecording = false
        }

        recorder.outputFilePath?.let {
            val file = File(it)
            if (file.exists()) file.delete()
        }

        binding.waveformView.clear()
        setState(State.IDLE)
    }

    private fun playRecordedAudio() {
        val path = recorder.outputFilePath ?: return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
            setOnCompletionListener {
            }
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
