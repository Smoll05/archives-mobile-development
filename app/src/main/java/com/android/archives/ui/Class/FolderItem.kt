package com.android.archives.ui.Class

import android.os.Parcel
import android.os.Parcelable

data class FolderItem(
    val title: String,
    val name: String,
    val iconRes: Int = 0,
    val coverImageUri: String? = null,
    val profileImageUri: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(name)
        parcel.writeInt(iconRes)
        parcel.writeString(coverImageUri)
        parcel.writeString(profileImageUri)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FolderItem> {
        override fun createFromParcel(parcel: Parcel): FolderItem = FolderItem(parcel)
        override fun newArray(size: Int): Array<FolderItem?> = arrayOfNulls(size)
    }
}
