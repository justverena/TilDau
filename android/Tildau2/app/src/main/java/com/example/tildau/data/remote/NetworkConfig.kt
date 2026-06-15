package com.example.tildau.data.remote

object NetworkConfig {

    fun baseUrl(): String {
        return if (isEmulator()) {
            "http://10.0.2.2:8080/"
        } else {
            "http://192.168.1.109:8080/"
        }
    }

    private fun isEmulator(): Boolean {
        return (
                android.os.Build.FINGERPRINT.startsWith("generic")
                        || android.os.Build.MODEL.contains("Emulator")
                        || android.os.Build.MODEL.contains("Android SDK built for x86")
                        || android.os.Build.MANUFACTURER.contains("Genymotion")
                        || android.os.Build.HARDWARE.contains("goldfish")
                        || android.os.Build.HARDWARE.contains("ranchu")
                )
    }
}