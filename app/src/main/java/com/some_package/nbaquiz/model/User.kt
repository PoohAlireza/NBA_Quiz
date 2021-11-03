package com.some_package.nbaquiz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User() : Parcelable {

    constructor(username:String,avatar:Int?,team:Int?,lowerCaseUsername:String):this(){
        this.username=username
        this.team=team
        this.avatar=avatar
        this.lowerCaseUsername=lowerCaseUsername
    }

    var username:String? = ""
    var lowerCaseUsername:String? = ""
    var id:String? = ""
    var points:Int? = 0
    var games:Int? = 0
    var wins:Int? = 0
    var avatar:Int? = 0
    var team:Int? = 0


}