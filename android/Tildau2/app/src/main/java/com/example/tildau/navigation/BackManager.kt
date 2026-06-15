package com.example.tildau.navigation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tildau.R

object BackManager {

    fun handle(context: Context) {

        val activity = context as? AppCompatActivity ?: return

        val navController =
            activity.findNavController(R.id.nav_host_fragment)

        val popped = navController.popBackStack()

        if (!popped) {
            activity.finish()
        }
    }
}