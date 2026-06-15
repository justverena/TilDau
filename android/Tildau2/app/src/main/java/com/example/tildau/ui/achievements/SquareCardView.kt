package com.example.tildau.ui.achievements

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

class SquareCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {

        super.onMeasure(
            widthMeasureSpec,
            widthMeasureSpec
        )
    }
}