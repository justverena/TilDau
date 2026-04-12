package com.example.tildau.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.model.next.NextStepResponse
import com.example.tildau.data.model.next.NextStepType
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.remote.CourseApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.data.repository.CourseRepository
import com.example.tildau.databinding.ActivityMainBinding
import com.example.tildau.databinding.ViewTapbarBinding
import com.example.tildau.ui.onboarding.DefectOnboardingActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tapbarBinding: ViewTapbarBinding
//    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tapbarBinding = binding.tapbar

        applyWindowInsets()

//        val api = ApiClient.createServiceWithToken(
//            AuthApi::class.java
//        ) { TokenManager.getToken(this) }
//
//        val repository = AuthRepository(api)
//
//        viewModel = ViewModelProvider(
//            this,
//            object : ViewModelProvider.Factory {
//                override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                    return MainViewModel(repository) as T
//                }
//            }
//        )[MainViewModel::class.java]

//        lifecycleScope.launch {
//            try {
//                val hasDefects = viewModel.hasDefects()
//
//                if (!hasDefects) {
//                    startActivity(
//                        Intent(this@MainActivity, DefectOnboardingActivity::class.java)
//                    )
//                    finish()
//                    return@launch
//                }
//                initNavigation()
//
//
//            } catch (e: Exception) {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Error: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val hiddenFragments = setOf(
                R.id.recordFragment,
                R.id.analyzeFragment,
                R.id.resultFragment
            )

            if (destination.id in hiddenFragments) {
                tapbarBinding.root.visibility = View.GONE
            } else {
                tapbarBinding.root.visibility = View.VISIBLE
            }
        }

        tapbarBinding.btnStart.setOnClickListener {
            startOrResumeCourse()
        }

        tapbarBinding.btnStats.setOnClickListener {
            navController.navigate(R.id.statisticsFragment)
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

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hidden = setOf(
                R.id.recordFragment,
                R.id.analyzeFragment,
                R.id.resultFragment
            )

            tapbarBinding.root.visibility =
                if (destination.id in hidden) View.GONE else View.VISIBLE
        }

        tapbarBinding.btnLesson.setOnClickListener {
            navController.navigate(R.id.coursesFragment)
        }
    }

    private fun startOrResumeCourse() {

        val navController = findNavController(R.id.nav_host_fragment)

        // ❗ тут нужен courseId
        val courseId = getCurrentCourseId()

        if (courseId == null) {
            Toast.makeText(this, "No course selected", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.coursesFragment)
            return
        }

        lifecycleScope.launch {
            try {
                val api = ApiClient.createServiceWithToken(
                    CourseApi::class.java
                ) { TokenManager.getToken(this@MainActivity) }

                val repository = CourseRepository(api)

                val nextStep = try {
                    repository.resumeCourse(courseId)
                } catch (e: Exception) {
                    null
                }

                val finalStep = nextStep ?: repository.startCourse(courseId)

                if (finalStep != null) {
                    handleNextStepGlobal(finalStep)
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleNextStepGlobal(nextStep: NextStepResponse) {
        val navController = findNavController(R.id.nav_host_fragment)

        when (nextStep.type) {

            NextStepType.EXERCISE,
            NextStepType.RETRY -> {
                nextStep.id?.let { id ->
                    val bundle = Bundle().apply {
                        putString("ARG_EXERCISE_ID", id)
                    }

                    navController.navigate(R.id.recordFragment, bundle)
                }
            }

            NextStepType.FINISH -> {
                Toast.makeText(this, "Course completed 🎉", Toast.LENGTH_LONG).show()
                navController.navigate(R.id.coursesFragment)
            }

            NextStepType.RESOURCE -> {
                Toast.makeText(this, "Resource not implemented", Toast.LENGTH_SHORT).show()
            }

            NextStepType.UNKNOWN -> {
                Toast.makeText(
                    this,
                    "Unknown step type",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCurrentCourseId(): String? {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getString("current_course_id", null)
    }

    private fun applyWindowInsets() {
        val root = binding.root
        val tapbarView = tapbarBinding.root
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingStart,
                systemBars.top,
                view.paddingEnd,
                view.paddingBottom
            )

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