package com.android.archives.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("file_prefs", Context.MODE_PRIVATE)

    fun saveFiles(files: List<Pair<String, String>>) {
        val set = files.map { "${it.first}|||${it.second}" }.toSet()
        prefs.edit().putStringSet("uploads", set).apply()
    }

    fun loadFiles(): List<Pair<String, String>> {
        val set = prefs.getStringSet("uploads", emptySet()) ?: emptySet()
        return set.mapNotNull {
            val parts = it.split("|||")
            if (parts.size == 2) parts[0] to parts[1] else null
        }
    }
    object UserSession {
        private const val PREFS_NAME = "UserPrefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CURRENT_USER = "current_user"

        fun saveUser(context: Context, email: String, password: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("user_${email}_email", email)
            editor.putString("user_${email}_password", password)
            editor.apply()
        }

        fun isUserValid(context: Context, email: String, password: String): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedEmail = prefs.getString("user_${email}_email", null)
            val savedPassword = prefs.getString("user_${email}_password", null)
            return email == savedEmail && password == savedPassword
        }

        fun setCurrentUser(context: Context, email: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_CURRENT_USER, email).apply()
        }

        fun getCurrentUser(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_CURRENT_USER, null)
        }

        fun clearAllUserData(context: Context) {
            val prefs = context.getSharedPreferences("uploaded_files", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }

}
