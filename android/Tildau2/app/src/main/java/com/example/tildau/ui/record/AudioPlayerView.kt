package com.example.tildau.ui.record

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
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

    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    private var isPrepared = false
    private var isPlaying = false

    private var onCompletion: (() -> Unit)? = null

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletion = listener
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_audio_player, this, true)

        btnPlay = findViewById(R.id.btnPlay)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvDuration = findViewById(R.id.tvDuration)

        btnPlay.setOnClickListener { toggle() }
    }

    // =========================
    // SET AUDIO (SAFE VERSION)
    // =========================
    fun setAudio(path: String, autoPlay: Boolean = false) {

        release()

        mediaPlayer = MediaPlayer().apply {

            setDataSource(path)
            prepareAsync()

            setOnPreparedListener { mp ->
                isPrepared = true

                progressBar.max = mp.duration
                tvDuration.text = formatTime(mp.duration)

                progressBar.progress = 0
                tvCurrentTime.text = formatTime(0)

                btnPlay.setImageResource(R.drawable.btn_play)

                // 🔥 автозапуск после подготовки
                if (autoPlay) {
                    playInternal()
                }
            }

            setOnCompletionListener {
                this@AudioPlayerView.isPlaying = false
                isPrepared = false

                btnPlay.setImageResource(R.drawable.btn_play)

                stopProgress()

                progressBar.progress = progressBar.max
                tvCurrentTime.text = formatTime(progressBar.max)

                onCompletion?.invoke()
            }
        }
    }

    // =========================
    // TOGGLE
    // =========================
    private fun toggle() {
        val player = mediaPlayer ?: return
        if (!isPrepared) return

        if (isPlaying) pauseInternal()
        else playInternal()
    }

    private fun playInternal() {
        mediaPlayer?.start()
        isPlaying = true

        btnPlay.setImageResource(R.drawable.btn_pause)

        startProgress()
    }

    private fun pauseInternal() {
        mediaPlayer?.pause()
        isPlaying = false

        btnPlay.setImageResource(R.drawable.btn_play)

        stopProgress()
    }

    // =========================
    // PROGRESS
    // =========================
    private fun startProgress() {
        updateRunnable = object : Runnable {
            override fun run() {
                val p = mediaPlayer ?: return

                val pos = p.currentPosition
                progressBar.progress = pos
                tvCurrentTime.text = formatTime(pos)

                handler.postDelayed(this, 250)
            }
        }
        handler.post(updateRunnable!!)
    }

    private fun stopProgress() {
        handler.removeCallbacks(updateRunnable ?: return)
        updateRunnable = null
    }

    // =========================
    // PUBLIC API
    // =========================
    fun play() {
        if (isPrepared) playInternal()
    }

    fun pause() {
        pauseInternal()
    }

    fun release() {
        stopProgress()

        mediaPlayer?.release()
        mediaPlayer = null

        isPrepared = false
        isPlaying = false

        btnPlay.setImageResource(R.drawable.btn_play)

        progressBar.progress = 0
        tvCurrentTime.text = formatTime(0)
    }

    // =========================
    // UTILS
    // =========================
    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return "%d:%02d".format(sec / 60, sec % 60)
    }
}