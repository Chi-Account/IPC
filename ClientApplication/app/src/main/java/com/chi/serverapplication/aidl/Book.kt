package com.chi.serverapplication.aidl

import android.os.Parcel
import android.os.Parcelable

data class Book(val name: String) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString() ?: "")

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}