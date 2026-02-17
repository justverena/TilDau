package com.example.tildau.ui.main


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.tildau.R
import com.example.tildau.databinding.ActivityMainBinding
import com.example.tildau.databinding.ViewTapbarBinding
import com.example.tildau.ui.courses.CoursesFragment
import com.example.tildau.ui.home.HomeFragment
import com.example.tildau.ui.profile.AccountFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tapbarBinding: ViewTapbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // binding include уже доступен через ActivityMainBinding
        tapbarBinding = binding.tapbar  // <--- используем правильное имя поля

        setupTapbar()
        applyWindowInsets()

        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }
    }

    private fun setupTapbar() {
        tapbarBinding.btnHome.setOnClickListener {
            openFragment(HomeFragment())
        }

        tapbarBinding.btnStart.setOnClickListener {
            openFragment(CoursesFragment())
        }

        tapbarBinding.btnProfile.setOnClickListener {
            openFragment(AccountFragment())
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (current !is HomeFragment) {
            openFragment(HomeFragment())
        } else {
            super.onBackPressed()
        }
    }

    private fun applyWindowInsets() {
        val root = binding.root
        val tapbarView = tapbarBinding.root  // это реальный View

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraBottom = resources.getDimensionPixelSize(R.dimen.tapbar_extra_bottom)

            // используем реальные padding свойства
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
