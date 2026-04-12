package com.example.tildau.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tildau.R
import com.example.tildau.data.local.TokenManager
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.AuthApi
import com.example.tildau.data.repository.AuthRepository
import com.example.tildau.ui.main.MainActivity
import kotlinx.coroutines.launch

class DefectOnboardingActivity : AppCompatActivity() {

    private lateinit var viewModel: DefectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DEFECT_FLOW", "ONCREATE onboarding")

        setupViewModel()
        showDefectDialog()
    }

    private fun setupViewModel() {
        val api = ApiClient.createServiceWithToken(
            AuthApi::class.java
        ) { TokenManager.getToken(this) }

        val repository = AuthRepository(api)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DefectViewModel(repository) as T
                }
            }
        )[DefectViewModel::class.java]
    }

    private fun showDefectDialog() {
        val view = layoutInflater.inflate(R.layout.sheet_defects, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        view.findViewById<android.widget.Button>(R.id.btnRhotacism)
            .setOnClickListener { selectDefect(1, dialog) }

        view.findViewById<android.widget.Button>(R.id.btnLambdacism)
            .setOnClickListener { selectDefect(2, dialog) }

        view.findViewById<android.widget.Button>(R.id.btnSigmatism)
            .setOnClickListener { selectDefect(3, dialog) }

        dialog.show()
    }

    private fun selectDefect(id: Int, dialog: androidx.appcompat.app.AlertDialog) {
        lifecycleScope.launch {
            try {
                viewModel.setDefect(id)

                val ok = viewModel.checkDefects()

                if (!ok) {
                    Toast.makeText(
                        this@DefectOnboardingActivity,
                        "Дефект не сохранился",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                dialog.dismiss()

                startActivity(
                    Intent(this@DefectOnboardingActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )

                finish()

            } catch (e: Exception) {
                Toast.makeText(this@DefectOnboardingActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}