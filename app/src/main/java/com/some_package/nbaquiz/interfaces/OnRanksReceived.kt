package com.some_package.nbaquiz.interfaces

import com.some_package.nbaquiz.model.User

interface OnRanksReceived {
    fun onReceived(userList : List<User>)
}