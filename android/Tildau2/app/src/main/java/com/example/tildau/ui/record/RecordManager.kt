package com.example.tildau.ui.record

class RecordManager(
    private val recorder: WavAudioRecorder
) {

    val outputPath: String?
        get() = recorder.outputFilePath

    var onAmplitudeChanged: ((Int) -> Unit)?
        get() = recorder.onAmplitudeChanged
        set(value) {
            recorder.onAmplitudeChanged = value
        }

    fun startRecording() {
        recorder.startRecording()
    }

    fun stopRecording() {
        recorder.stopRecording()
    }

    fun cancelRecording() {
        recorder.stopRecording()
    }
}