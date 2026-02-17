package com.example.tildau.ui.record

import android.content.Context
import android.media.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint


class WavAudioRecorder(private val context: Context) {

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingThread: Thread? = null

    var outputFilePath: String? = null
        private set

    var onAmplitudeChanged: ((Int) -> Unit)? = null


    @SuppressLint("MissingPermission")
    fun startRecording() {

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig,
            audioFormat
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        val file = File(context.cacheDir, "audio_$timeStamp.wav")
        outputFilePath = file.absolutePath

        audioRecord?.startRecording()
        isRecording = true

        recordingThread = Thread {
            writeAudioData(file, bufferSize)
        }
        recordingThread?.start()
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingThread = null
    }

    private fun writeAudioData(file: File, bufferSize: Int) {



        val buffer = ByteArray(bufferSize)
        var totalAudioLen = 0

        try {
            FileOutputStream(file).use { fos ->

                writeWavHeader(fos, 0)

                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                    if (read > 0) {

                        var max = 0
                        for (i in buffer.indices step 2) {
                            val value = (buffer[i].toInt() and 0xff) or
                                    (buffer[i + 1].toInt() shl 8)
                            if (value > max) max = value
                        }

                        onAmplitudeChanged?.invoke(max)

                        fos.write(buffer, 0, read)
                        totalAudioLen += read
                    }

                    if (read > 0) {
                        fos.write(buffer, 0, read)
                        totalAudioLen += read
                    }
                }

                fos.flush()
            }

            RandomAccessFile(file, "rw").use { raf ->
                writeWavHeaderRandomAccess(raf, totalAudioLen)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun writeWavHeader(out: OutputStream, totalAudioLen: Int) {

        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2

        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()

        writeInt(header, 4, totalDataLen)

        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()

        writeInt(header, 16, 16)
        writeShort(header, 20, 1)
        writeShort(header, 22, 1)
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRate)
        writeShort(header, 32, 2)
        writeShort(header, 34, 16)

        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()

        writeInt(header, 40, totalAudioLen)

        out.write(header, 0, 44)
    }

    private fun writeWavHeaderRandomAccess(raf: RandomAccessFile, totalAudioLen: Int) {
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2

        raf.seek(4)
        raf.writeInt(Integer.reverseBytes(totalDataLen))

        raf.seek(40)
        raf.writeInt(Integer.reverseBytes(totalAudioLen))
    }

    private fun writeInt(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xff).toByte()
        data[offset + 1] = ((value shr 8) and 0xff).toByte()
        data[offset + 2] = ((value shr 16) and 0xff).toByte()
        data[offset + 3] = ((value shr 24) and 0xff).toByte()
    }

    private fun writeShort(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xff).toByte()
        data[offset + 1] = ((value shr 8) and 0xff).toByte()
    }
}
