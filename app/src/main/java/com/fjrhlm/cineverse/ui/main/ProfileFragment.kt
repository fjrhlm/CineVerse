package com.fjrhlm.cineverse.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fjrhlm.cineverse.R
import com.fjrhlm.cineverse.data.local.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnEditProfile: RelativeLayout
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var btnLanguage: RelativeLayout
    private lateinit var btnSupport: RelativeLayout
    private lateinit var btnLogout: MaterialButton

    private lateinit var tvEditProfileLabel: TextView
    private lateinit var tvDarkModeLabel: TextView
    private lateinit var tvNotificationsLabel: TextView
    private lateinit var tvLanguageLabel: TextView
    private lateinit var tvLanguageValue: TextView
    private lateinit var tvSupportLabel: TextView
    private lateinit var ivProfileAvatar: ImageView

    private lateinit var sessionManager: SessionManager

    private val pickImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            sessionManager.setCustomAvatarUri(it.toString())
            loadAvatar()
            val isIndo = sessionManager.getLanguage() == "id"
            val successMsg = if (isIndo) "Foto profil diperbarui" else "Profile photo updated"
            Toast.makeText(requireContext(), successMsg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        tvUsername = view.findViewById(R.id.tv_username)
        tvEmail = view.findViewById(R.id.tv_email)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        switchDarkMode = view.findViewById(R.id.switch_dark_mode)
        switchNotifications = view.findViewById(R.id.switch_notifications)
        btnLanguage = view.findViewById(R.id.btn_language)
        btnSupport = view.findViewById(R.id.btn_support)
        btnLogout = view.findViewById(R.id.btn_logout)

        tvEditProfileLabel = view.findViewById(R.id.tv_edit_profile_label)
        tvDarkModeLabel = view.findViewById(R.id.tv_dark_mode_label)
        tvNotificationsLabel = view.findViewById(R.id.tv_notifications_label)
        tvLanguageLabel = view.findViewById(R.id.tv_language_label)
        tvLanguageValue = view.findViewById(R.id.tv_language_value)
        tvSupportLabel = view.findViewById(R.id.tv_support_label)
        ivProfileAvatar = view.findViewById(R.id.iv_profile_avatar)

        // Bind Session Info
        tvUsername.text = sessionManager.getUsername()
        tvEmail.text = sessionManager.getEmail() ?: "user@cineverse.com"

        // Initialize Settings from Preferences
        loadAvatar()
        updateLanguageUI(sessionManager.getLanguage())
        
        // Prevent toggle listener triggering on init
        switchDarkMode.isChecked = sessionManager.isDarkMode()

        // Setup interaction listeners
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val lang = sessionManager.getLanguage()
            val notifStr = if (isChecked) {
                if (lang == "id") "Diaktifkan" else "Enabled"
            } else {
                if (lang == "id") "Dinonaktifkan" else "Disabled"
            }
            Toast.makeText(requireContext(), "Notifications: $notifStr", Toast.LENGTH_SHORT).show()
        }

        btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        btnSupport.setOnClickListener {
            val lang = sessionManager.getLanguage()
            val supportMsg = if (lang == "id") "Menghubungi Dukungan..." else "Contacting Support..."
            Toast.makeText(requireContext(), supportMsg, Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            sessionManager.logoutUser()
            val lang = sessionManager.getLanguage()
            val logoutMsg = if (lang == "id") "Berhasil keluar" else "Logged out successfully"
            Toast.makeText(requireContext(), logoutMsg, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun loadAvatar() {
        val customUri = sessionManager.getCustomAvatarUri()
        if (customUri != null) {
            Glide.with(this)
                .load(android.net.Uri.parse(customUri))
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .circleCrop()
                .into(ivProfileAvatar)
        } else {
            val avatarIdx = sessionManager.getAvatarIndex()
            val isBoy = avatarIdx % 2 == 1
            val gender = if (isBoy) "boy" else "girl"
            val avatarUrl = "https://avatar.iran.liara.run/public/$gender?id=$avatarIdx"

            Glide.with(this)
                .load(avatarUrl)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .circleCrop()
                .into(ivProfileAvatar)
        }
    }

    private fun updateLanguageUI(lang: String) {
        if (lang == "id") {
            tvLanguageValue.text = "Bahasa Indonesia"
            tvEditProfileLabel.text = "Edit Profil"
            tvDarkModeLabel.text = "Mode Gelap"
            tvNotificationsLabel.text = "Notifikasi"
            tvLanguageLabel.text = "Bahasa"
            tvSupportLabel.text = "Hubungi Dukungan"
            btnLogout.text = "Keluar"
        } else {
            tvLanguageValue.text = "English"
            tvEditProfileLabel.text = "Edit Profile"
            tvDarkModeLabel.text = "Dark Mode"
            tvNotificationsLabel.text = "Notifications"
            tvLanguageLabel.text = "Language"
            tvSupportLabel.text = "Contact Support"
            btnLogout.text = "Logout"
        }
    }

    private fun showLanguageDialog() {
        val options = arrayOf("English", "Bahasa Indonesia")
        val currentLang = sessionManager.getLanguage()
        val checkedItem = if (currentLang == "id") 1 else 0

        AlertDialog.Builder(requireContext(), R.style.Theme_CineVerse)
            .setTitle(if (currentLang == "id") "Pilih Bahasa" else "Select Language")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                val newLang = if (which == 1) "id" else "en"
                sessionManager.setLanguage(newLang)
                updateLanguageUI(newLang)
                val toastMsg = if (newLang == "id") "Bahasa diubah ke Bahasa Indonesia" else "Language changed to English"
                Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(if (currentLang == "id") "Batal" else "Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showEditProfileDialog() {
        val context = requireContext()
        val currentLang = sessionManager.getLanguage()
        val builder = AlertDialog.Builder(context, R.style.Theme_CineVerse)
        builder.setTitle(if (currentLang == "id") "Edit Profil" else "Edit Profile")

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        // Username Label & EditText
        val tvUserLabel = TextView(context).apply {
            text = "Username"
            setTextColor(ContextCompat.getColor(context, R.color.teks_utama))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        val etUser = EditText(context).apply {
            setText(sessionManager.getUsername())
            setTextColor(ContextCompat.getColor(context, R.color.teks_utama))
        }

        // Email Label & EditText
        val tvEmailLabel = TextView(context).apply {
            text = "Email"
            setTextColor(ContextCompat.getColor(context, R.color.teks_utama))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 24
            }
        }
        val etEmail = EditText(context).apply {
            setText(sessionManager.getEmail())
            setTextColor(ContextCompat.getColor(context, R.color.teks_utama))
        }

        var alertDialog: AlertDialog? = null

        val btnPickGallery = MaterialButton(context, null, android.R.attr.borderlessButtonStyle).apply {
            text = if (currentLang == "id") "Pilih dari Galeri" else "Choose from Gallery"
            setTextColor(ContextCompat.getColor(context, R.color.aksen_amber))
            setIconResource(android.R.drawable.ic_menu_camera)
            setIconTint(ContextCompat.getColorStateList(context, R.color.aksen_amber))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = 24
            }
            setOnClickListener {
                pickImageLauncher.launch("image/*")
                alertDialog?.dismiss()
            }
        }

        container.addView(tvUserLabel)
        container.addView(etUser)
        container.addView(tvEmailLabel)
        container.addView(etEmail)
        container.addView(btnPickGallery)

        builder.setView(container)

        builder.setPositiveButton(if (currentLang == "id") "Simpan" else "Save") { dialog, _ ->
            val newUsername = etUser.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()

            if (newUsername.isNotEmpty() && newEmail.isNotEmpty()) {
                sessionManager.updateProfile(newUsername, newEmail)
                
                tvUsername.text = newUsername
                tvEmail.text = newEmail
                loadAvatar()

                val successMsg = if (currentLang == "id") "Profil diperbarui" else "Profile updated"
                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            } else {
                val errorMsg = if (currentLang == "id") {
                    "Username dan Email tidak boleh kosong"
                } else {
                    "Username and Email cannot be empty"
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(if (currentLang == "id") "Batal" else "Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog.show()
    }
}
