package com.android.archives.ui.Class

import android.net.Uri

data class UploadedDocument(
    val name: String,
    val uriString: String
) {
    val uri: Uri get() = Uri.parse(uriString)
}
