package com.example.tildau.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tildau.databinding.FragmentAccountBinding
import com.example.tildau.ui.login.AuthActivity
import androidx.navigation.fragment.findNavController
import com.example.tildau.R
import com.example.tildau.data.remote.ApiClient
import com.example.tildau.data.remote.UserApi
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var userApi: UserApi
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User")

        val token = prefs.getString("jwt_token", null)

        userApi = ApiClient.createServiceWithToken(
            UserApi::class.java
        ) { token }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = userApi.getProfile()

                if (_binding != null) {
                    binding.userNameText.text = user.name
                }

            } catch (e: Exception) {
                e.printStackTrace()

                if (_binding != null) {
                    binding.userNameText.text = "User"
                }
            }
        }

        binding.userNameText.text = userName

        binding.profileRow.setOnClickListener {
            val intent = Intent(requireContext(), ProfileViewActivity::class.java)
            startActivity(intent)
        }

        binding.notificationsRow.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }

        binding.deleteButton.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.statisticsRow.setOnClickListener {
            findNavController().navigate(R.id.statisticsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
