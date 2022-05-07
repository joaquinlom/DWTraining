package com.dwtraining.lom.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fugitive(val id: Int = 0, var name: String = "", var status: Int = 0,var photo: String? = "", var notification: Int = 0) : Parcelable

@Parcelize
data class DeletionLog(val name: String = "", val date: String = "",val status:Int = 0, var notification: Int = 0) : Parcelable
