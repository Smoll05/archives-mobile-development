package com.android.archives.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.android.archives.data.model.File

class FileHelper {
    companion object {
        fun getFileName(context: Context, uri: Uri): String {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) return cursor.getString(nameIndex)
            }
            return "unknown_file"
        }

        suspend fun deleteFileFromStorage(context: Context, file: File) {
            try {
                val fileToDelete = java.io.File(context.filesDir, file.filePath)
                if (fileToDelete.exists()) {
                    val deleted = fileToDelete.delete()
                    if (deleted) {
                        Log.d("FileDeletion", "File successfully deleted from storage.")
                    } else {
                        Log.e("FileDeletion", "Failed to delete the file from storage.")
                    }
                }
            } catch (e: Exception) {
                Log.e("FileDeletion", "Error deleting file: ${e.message}")
            }
        }
    }
}