package com.fjrhlm.cineverse.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "CineVerseSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_AVATAR_INDEX = "avatarIndex"
        private const val KEY_DARK_MODE = "isDarkMode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CUSTOM_AVATAR_URI = "customAvatarUri"
        private const val KEY_AUTH_TOKEN = "authToken" // Baru untuk Token API Web Admin
    }

    fun createLoginSession(username: String, email: String, token: String = "") {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        if (token.isNotEmpty()) {
            editor.putString(KEY_AUTH_TOKEN, token)
        }
        editor.putInt(KEY_AVATAR_INDEX, 1) // Default avatar
        editor.putBoolean(KEY_DARK_MODE, true) // Default dark mode enabled
        editor.putString(KEY_LANGUAGE, "en") // Default English
        editor.putString(KEY_CUSTOM_AVATAR_URI, null) // No custom photo initially
        editor.commit()
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUsername(): String? {
        return pref.getString(KEY_USERNAME, "User")
    }

    fun getEmail(): String? {
        return pref.getString(KEY_EMAIL, null)
    }
    
    fun getToken(): String? {
        return pref.getString(KEY_AUTH_TOKEN, null)
    }

    fun getAvatarIndex(): Int {
        return pref.getInt(KEY_AVATAR_INDEX, 1)
    }

    fun setAvatarIndex(index: Int) {
        editor.putInt(KEY_AVATAR_INDEX, index)
        editor.commit()
    }

    fun isDarkMode(): Boolean {
        return pref.getBoolean(KEY_DARK_MODE, true)
    }

    fun setDarkMode(isDark: Boolean) {
        editor.putBoolean(KEY_DARK_MODE, isDark)
        editor.commit()
    }

    fun getLanguage(): String {
        return pref.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(lang: String) {
        editor.putString(KEY_LANGUAGE, lang)
        editor.commit()
    }

    fun getCustomAvatarUri(): String? {
        return pref.getString(KEY_CUSTOM_AVATAR_URI, null)
    }

    fun setCustomAvatarUri(uriStr: String?) {
        editor.putString(KEY_CUSTOM_AVATAR_URI, uriStr)
        editor.commit()
    }

    fun updateProfile(username: String, email: String) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        editor.commit()
    }

    fun logoutUser() {
        val currentDarkMode = isDarkMode()
        val currentLang = getLanguage()
        
        editor.clear()
        editor.commit()
        
        // Restore settings so they aren't lost on logout
        setDarkMode(currentDarkMode)
        setLanguage(currentLang)
    }
}
