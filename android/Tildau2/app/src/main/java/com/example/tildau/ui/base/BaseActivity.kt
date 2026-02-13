package com.example.tildau.ui.base

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tildau.R
import com.example.tildau.ui.home.HomeActivity
import com.example.tildau.ui.profile.AccountActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tildau.ui.course.CoursesActivity


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
            startActivity(Intent(this, CoursesActivity::class.java))
        }

//        findViewById<View>(R.id.btnStart).setOnClickListener {
//            startActivity(Intent(this, StartActivityExample::class.java))
//        }

        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
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
