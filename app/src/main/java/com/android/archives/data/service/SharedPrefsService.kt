package com.android.archives.data.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefsService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFS_NAME = "UserPrefs"
    private val KEY_CURRENT_USER = "current_user"
    private val NOTIFICATION_ENABLED_KEY = "notification_enabled"

    fun saveUser(email: String, password: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("user_${email}_email", email)
        editor.putString("user_${email}_password", password)
        editor.apply()
    }

    fun isUserValid(email: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("user_${email}_email", null)
        val savedPassword = prefs.getString("user_${email}_password", null)
        return email == savedEmail && password == savedPassword
    }

    fun setCurrentUser(userId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_CURRENT_USER, userId).apply()
    }

    fun getCurrentUser(): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_CURRENT_USER, -1L)
    }

    fun clearCurrentUser() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }


    fun clearAllUserData() {
        val prefs = context.getSharedPreferences("uploaded_files", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun isUserLoggedIn() : Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_CURRENT_USER, -1L) != -1L
    }
}
