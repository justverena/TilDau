package com.example.tildau.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

class LoadingWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val numberOfBars = 16
    private val barColors = IntArray(numberOfBars)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val handler = Handler(Looper.getMainLooper())
    private var currentStep = 0
    private val stepDuration = 100L

    private var isRunning = false

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 14f

        val baseColor = 0xFF6C47EC.toInt()
        for (i in 0 until numberOfBars) {
            val alpha = ((numberOfBars - i) * 255 / numberOfBars).coerceIn(0, 255)
            barColors[i] = (alpha shl 24) or (baseColor and 0x00FFFFFF)
        }
    }

    private val stepRunnable = object : Runnable {
        override fun run() {
            currentStep = (currentStep + 1) % numberOfBars
            invalidate()
            if (isRunning) {
                handler.postDelayed(this, stepDuration)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f - paint.strokeWidth

        val angleStep = 360f / numberOfBars

        for (i in 0 until numberOfBars) {
            val index = (i + currentStep) % numberOfBars
            paint.color = barColors[index]

            canvas.save()
            canvas.rotate(i * angleStep, cx, cy)
            canvas.drawLine(
                cx,
                cy - radius,
                cx,
                cy - radius * 0.5f,
                paint)
            canvas.restore()
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            handler.post(stepRunnable)
        }
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(stepRunnable)
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