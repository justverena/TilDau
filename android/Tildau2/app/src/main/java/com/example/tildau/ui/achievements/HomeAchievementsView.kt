package com.example.tildau.ui.achievements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.example.tildau.databinding.ViewHomeAchievementsBinding
import android.view.animation.DecelerateInterpolator

class HomeAchievementsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val handler =
        Handler(Looper.getMainLooper())

    private var currentPage = 0

    private var achievements =
        emptyList<AchievementType>()

    var onAchievementsClicked: (() -> Unit)? = null

    private val autoScrollRunnable =
        object : Runnable {

            override fun run() {

                if (achievements.isEmpty()) return

                currentPage++

                if (currentPage >= achievements.size) {
                    currentPage = 0
                }

                binding.viewPager.setCurrentItem(currentPage, true)

                handler.postDelayed(this, 6000)
            }
        }


    private val pageCallback =
        object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(
                position: Int
            ) {

                currentPage = position

                updateDots(position)
            }
        }
    private val binding =
        ViewHomeAchievementsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL

        binding.root.setOnClickListener {
            onAchievementsClicked?.invoke()
        }

        binding.viewPager.setPageTransformer { page, position ->

            val absPos = kotlin.math.abs(position)

            page.alpha = 0.7f + (1f - absPos) * 0.3f

            page.scaleY = 0.95f + (1f - absPos) * 0.05f
        }
    }


    fun submitList(
        types: List<AchievementType>
    ) {

        achievements = types

        binding.viewPager.adapter =
            AchievementCarouselAdapter(types)

        createDots(types.size)

        binding.viewPager.unregisterOnPageChangeCallback(
            pageCallback
        )

        binding.viewPager.registerOnPageChangeCallback(
            pageCallback
        )

        handler.removeCallbacks(
            autoScrollRunnable
        )

        handler.postDelayed(
            autoScrollRunnable,
            3000
        )
    }

    private fun createDots(
        count: Int
    ) {

        binding.dotsContainer.removeAllViews()

        repeat(count) {

            val dot = ImageView(context)

            dot.setImageResource(
                android.R.drawable.presence_invisible
            )

            val params =
                LayoutParams(
                    24,
                    24
                )

            params.marginEnd = 8

            dot.layoutParams = params

            binding.dotsContainer.addView(dot)
        }

        updateDots(0)
    }

    private fun updateDots(
        selected: Int
    ) {

        for (i in 0 until binding.dotsContainer.childCount) {

            val dot =
                binding.dotsContainer
                    .getChildAt(i) as ImageView

            val drawable =
                if (i == selected) {
                    android.R.drawable.presence_online
                } else {
                    android.R.drawable.presence_invisible
                }

            dot.setImageResource(drawable)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        handler.removeCallbacks(
            autoScrollRunnable
        )
    }




}