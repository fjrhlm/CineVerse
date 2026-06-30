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
import com.fjrhlm.cineverse.data.local.SessionManager
import com.fjrhlm.cineverse.data.remote.BackendApiClient
import com.fjrhlm.cineverse.data.remote.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegisterLink: TextView
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        etEmail = view.findViewById(R.id.et_email)
        etPassword = view.findViewById(R.id.et_password)
        btnLogin = view.findViewById(R.id.btn_login)
        tvRegisterLink = view.findViewById(R.id.tv_register_link)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Loading..."

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = LoginRequest(email, password)
                    val response = BackendApiClient.instance.login(request)
                    
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Masuk"
                        
                        if (response.isSuccessful && response.body()?.accessToken != null) {
                            val authResponse = response.body()
                            val username = authResponse?.user?.userMetadata?.username ?: "User"
                            val userEmail = authResponse?.user?.email ?: email
                            val token = authResponse?.accessToken ?: ""
                            
                            sessionManager.createLoginSession(username, userEmail, token)
                            
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Login failed: Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Masuk"
                        Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}
