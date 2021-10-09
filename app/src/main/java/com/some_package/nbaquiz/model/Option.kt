package com.some_package.nbaquiz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Option : Parcelable {

    var id:Int?=null
    var choice:String?=null

}