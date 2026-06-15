package com.example.tildau.ui.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tildau.R
import com.example.tildau.databinding.FragmentNotificationsBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: NotificationPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNotificationsBinding.bind(view)
        prefs = NotificationPreferences(requireContext())

        loadData()

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.setEnabled(isChecked)

            if (isChecked) {
                scheduleNotification()
            } else {
                NotificationScheduler.cancel(requireContext())
            }
        }

        binding.timeContainer.setOnClickListener {
            showMaterialTimePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            scheduleNotification()
        }
    }

    private fun loadData() {
        binding.switchNotifications.isChecked = prefs.isEnabled()
        updateTimeText()
    }

    // 🔥 ВОТ ЭТО И ЕСТЬ НОРМАЛЬНЫЙ UI БУДИЛЬНИКА
    private fun showMaterialTimePicker() {

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(prefs.getHour())
            .setMinute(prefs.getMinute())
            .setTitleText("Уақытты таңдаңыз")
            .build()

        picker.show(parentFragmentManager, "time_picker")

        picker.addOnPositiveButtonClickListener {

            prefs.saveTime(picker.hour, picker.minute)
            updateTimeText()

            if (prefs.isEnabled()) {
                scheduleNotification()
            }
        }
    }

    private fun updateTimeText() {
        binding.tvTime.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            prefs.getHour(),
            prefs.getMinute()
        )
    }

    private fun scheduleNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                return
            }
        }

        NotificationScheduler.schedule(
            requireContext(),
            prefs.getHour(),
            prefs.getMinute()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}