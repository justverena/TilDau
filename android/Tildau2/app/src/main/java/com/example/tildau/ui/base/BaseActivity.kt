package com.example.tildau.ui.base

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tildau.R
import com.example.tildau.ui.profile.AccountFragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tildau.ui.courses.CoursesFragment
import com.example.tildau.ui.home.HomeFragment


abstract class BaseActivity : AppCompatActivity() {

    protected fun setBaseContent(layoutResId: Int) {
        setContentView(R.layout.activity_base)

        val container = findViewById<FrameLayout>(R.id.contentContainer)
        layoutInflater.inflate(layoutResId, container, true)

        setupTapbar()
        applyWindowInsets()
    }

    private fun setupTapbar() {
        findViewById<View>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.btnStart).setOnClickListener {
            val intent = Intent(this, CoursesFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, AccountFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun applyWindowInsets() {
        val root = findViewById<View>(R.id.root) ?: return
        val tapbar = findViewById<View>(R.id.tapbar) ?: return

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.tapbar_extra_top)
            val extraBottom = resources.getDimensionPixelSize(R.dimen.tapbar_extra_bottom)

            tapbar.setPadding(
                tapbar.paddingLeft,
                systemBars.top,
                tapbar.paddingRight,
                systemBars.bottom + extraBottom
            )

            insets
        }
    }

}
