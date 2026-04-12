package com.example.tildau.ui.record

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.tildau.R

class AudioPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val btnPlay: ImageButton
    private val progressBar: ProgressBar
    private val tvCurrentTime: TextView
    private val tvDuration: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var audioPath: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private var isAudioPlaying = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_audio_player, this, true)

        btnPlay = findViewById(R.id.btnPlay)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvDuration = findViewById(R.id.tvDuration)

        btnPlay.setOnClickListener {
            togglePlayback()
        }
    }

    fun setAudio(path: String) {
        release()

        audioPath = path

        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepareAsync()

            setOnPreparedListener { mp ->
                progressBar.max = mp.duration
                tvDuration.text = formatTime(mp.duration)
            }

            setOnCompletionListener {
                isAudioPlaying = false
                btnPlay.setImageResource(R.drawable.btn_play)
                handler.removeCallbacks(updateRunnable)

                progressBar.progress = progressBar.max
                tvCurrentTime.text = formatTime(progressBar.max)
            }
        }
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        release()
    }

    private fun togglePlayback() {
        val player = mediaPlayer ?: return

        if (isAudioPlaying) {
            player.pause()
            btnPlay.setImageResource(R.drawable.btn_play)
        } else {
            player.start()
            btnPlay.setImageResource(R.drawable.btn_pause)
            handler.post(updateRunnable)
        }

        isAudioPlaying = !isAudioPlaying
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val player = mediaPlayer ?: return

            val pos = player.currentPosition.coerceAtMost(progressBar.max)
            progressBar.progress = pos
            tvCurrentTime.text = formatTime(pos)

            handler.postDelayed(this, 300)
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateRunnable)
        isAudioPlaying = false
    }
}