package com.some_package.nbaquiz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Question : Parcelable {

    var id:Int? = null
    var question:String? =null
    var options:List<Option>? = null
    var answer:Int? = null

}