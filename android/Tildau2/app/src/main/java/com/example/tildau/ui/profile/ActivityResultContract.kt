package com.example.tildau.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class ProfileEditContract : ActivityResultContract<Intent, Pair<String, String>?>() {
    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<String, String>? {
        if (resultCode != android.app.Activity.RESULT_OK || intent == null) return null
        val field = intent.getStringExtra("field") ?: return null
        val value = intent.getStringExtra("value") ?: return null
        return field to value
    }
}
