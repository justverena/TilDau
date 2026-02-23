package com.example.tildau.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.tildau.R
import com.example.tildau.databinding.ActivityMainBinding
import com.example.tildau.databinding.ViewTapbarBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tapbarBinding: ViewTapbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tapbarBinding = binding.tapbar

        applyWindowInsets()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            navController.navigate(R.id.resultFragment)
        }

        tapbarBinding.btnStats.setOnClickListener {
            navController.navigate(R.id.resultFragment)
        }
        tapbarBinding.btnLesson.setOnClickListener {
            navController.navigate(R.id.coursesFragment)
        }
        tapbarBinding.btnProfile.setOnClickListener {
            navController.navigate(R.id.accountFragment)
        }
    }

    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }

    private fun applyWindowInsets() {
        val root = binding.root
        val tapbarView = tapbarBinding.root
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraBottom = resources.getDimensionPixelSize(R.dimen.tapbar_extra_bottom)
            tapbarView.setPadding(
                tapbarView.paddingStart,
                tapbarView.paddingTop,
                tapbarView.paddingEnd,
                systemBars.bottom + extraBottom
            )
            insets
        }
    }
}