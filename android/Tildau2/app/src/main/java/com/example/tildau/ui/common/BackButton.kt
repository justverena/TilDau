package com.example.tildau.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.tildau.R
import com.example.tildau.databinding.ViewBackButtonBinding
import com.example.tildau.navigation.BackManager

class BackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding =
        ViewBackButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.root.setOnClickListener {
            BackManager.handle(context)
        }
    }
}