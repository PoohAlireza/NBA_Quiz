package com.some_package.nbaquiz.repository

import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.preferences.UserSharedPref
import javax.inject.Inject


class LocalRepository @Inject constructor(private val userSharedPref: UserSharedPref) {

    fun getUserFromPref(): User {
        return userSharedPref.getUserPref()
    }
}