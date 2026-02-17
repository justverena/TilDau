package com.example.tildau.ui.record

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import kotlin.math.min

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val amplitudes = mutableListOf<Float>()
    private val idleAmplitudes = mutableListOf<Float>()

    private val barWidth = 8f
    private val barSpace = 12f

    private val paint = Paint().apply {
        color = Color.parseColor("#AF99FF")
        strokeCap = Paint.Cap.ROUND
        strokeWidth = barWidth
        isAntiAlias = true
    }

    private var idleInitialized = false

    fun addAmplitude(amplitude: Int) {
        val normalized = min(abs(amplitude) / 15000f, 1f)
        amplitudes.add(normalized)

        val maxBars = (width / barSpace).toInt()
        if (amplitudes.size > maxBars) amplitudes.removeAt(0)

        invalidate()
    }

    fun clear() {
        amplitudes.clear()
        invalidate()
    }

    private fun initIdle() {
        if (width == 0 || idleInitialized) return

        idleAmplitudes.clear()
        val maxBars = (width / barSpace).toInt()
        repeat(maxBars) {
            idleAmplitudes.add(0.05f + (0..0).random() / 200f)
        }
        idleInitialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!idleInitialized) initIdle()

        val centerY = height / 2f
        var x = width - barSpace

        val dataToDraw = if (amplitudes.isNotEmpty()) amplitudes else idleAmplitudes

        for (i in dataToDraw.indices.reversed()) {
            val amp = dataToDraw[i]
            val barHeight = amp * height * 0.8f

            canvas.drawLine(
                x,
                centerY - barHeight / 2,
                x,
                centerY + barHeight / 2,
                paint
            )
            x -= barSpace
            if (x < 0) break
        }
    }
}
