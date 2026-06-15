package com.example.tildau.ui.record

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.tildau.R
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val btnPlay: ImageButton
    private val progressBar: ProgressBar
    private val tvCurrentTime: TextView
    private val tvDuration: TextView

    private var mediaPlayer: MediaPlayer? = null

    private var exoPlayer: ExoPlayer? = null

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




    fun setAudio(path: String, autoPlay: Boolean = false) {

        release()

        exoPlayer = ExoPlayer.Builder(context).build().apply {

            val mediaItem = MediaItem.fromUri(path)
            setMediaItem(mediaItem)

            prepare()

            progressBar.max = 0
            tvCurrentTime.text = "0:00"
            tvDuration.text = "0:00"

            addListener(object : androidx.media3.common.Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    this@AudioPlayerView.isPlaying = isPlaying
                    btnPlay.setImageResource(
                        if (isPlaying) R.drawable.btn_pause
                        else R.drawable.btn_play
                    )

                    if (isPlaying) startProgress()
                    else stopProgress()
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == androidx.media3.common.Player.STATE_READY) {

                        val duration = duration.coerceAtLeast(0)

                        progressBar.max = duration.toInt()
                        tvDuration.text = formatTime(duration.toInt())
                    }

                    if (state == androidx.media3.common.Player.STATE_ENDED) {
                        stopProgress()
                        seekTo(0)
                        pause()
                        onCompletion?.invoke()
                    }
                }
            })

            if (autoPlay) play()
        }
    }




    private fun toggle() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause()
            else it.play()
        }
    }

    private fun playInternal() {
        exoPlayer?.play()
    }

    private fun pauseInternal() {
        exoPlayer?.pause()
    }




    private fun startProgress() {
        updateRunnable = object : Runnable {
            override fun run() {

                val p = exoPlayer ?: return

                val pos = p.currentPosition
                progressBar.progress = pos.toInt()
                tvCurrentTime.text = formatTime(pos.toInt())

                handler.postDelayed(this, 250)
            }
        }
        handler.post(updateRunnable!!)
    }

    private fun stopProgress() {
        handler.removeCallbacks(updateRunnable ?: return)
        updateRunnable = null
    }





    fun pause() {
        pauseInternal()
    }

    fun release() {

        stopProgress()

        exoPlayer?.release()
        exoPlayer = null

        isPrepared = false
        isPlaying = false

        btnPlay.setImageResource(R.drawable.btn_play)

        progressBar.progress = 0
        tvCurrentTime.text = "0:00"
    }



    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return "%d:%02d".format(sec / 60, sec % 60)
    }
}