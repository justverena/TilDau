package com.example.tildau.ui.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.tildau.databinding.ViewLoadingBinding

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding =
        ViewLoadingBinding.inflate(LayoutInflater.from(context), this)

    // ======== Тексты ========

    fun setTitle(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.titleText.visibility = GONE
        } else {
            binding.titleText.visibility = VISIBLE
            binding.titleText.text = text
        }
    }

    fun setSubtitle(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.subtitleText.visibility = GONE
        } else {
            binding.subtitleText.visibility = VISIBLE
            binding.subtitleText.text = text
        }
    }

    // ======== Проксируем запуск к LoadingWheelView ========

    fun start() {
        binding.spinnerImage.start()
    }

    fun stop() {
        binding.spinnerImage.stop()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }
}