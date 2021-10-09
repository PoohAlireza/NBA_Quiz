package com.some_package.nbaquiz.interfaces

import com.some_package.nbaquiz.model.User

interface OnSearchUserDone {
    fun onDone(userList : List<User>)
}