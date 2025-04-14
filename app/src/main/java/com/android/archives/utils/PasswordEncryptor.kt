package com.android.archives.utils

import java.security.MessageDigest

class PasswordEncryptor {
    companion object {
        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.joinToString("") { "%02x".format(it) }
        }

    }
}