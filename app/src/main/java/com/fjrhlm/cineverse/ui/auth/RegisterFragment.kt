package com.fjrhlm.cineverse.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.remote.BackendApiClient
import com.fjrhlm.cineverse.data.remote.RegisterRequest
import com.fjrhlm.cineverse.data.remote.RegisterMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLoginLink: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUsername = view.findViewById(R.id.et_username)
        etEmail = view.findViewById(R.id.et_email)
        etPassword = view.findViewById(R.id.et_password)
        btnRegister = view.findViewById(R.id.btn_register)
        tvLoginLink = view.findViewById(R.id.tv_login_link)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Loading..."

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Pendaftaran ke Supabase mengirimkan email, password, dan metadata (username)
                    val request = RegisterRequest(email, password, RegisterMetadata(username))
                    val response = BackendApiClient.instance.register(request)
                    
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Daftar"
                        
                        if (response.isSuccessful && response.body()?.user != null) {
                            Toast.makeText(requireContext(), "Registration successful! Please check your email if confirmation is enabled.", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(requireContext(), "Registration failed: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Daftar"
                        Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}
