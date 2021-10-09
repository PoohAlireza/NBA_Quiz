package com.some_package.nbaquiz.interfaces

import com.some_package.nbaquiz.model.User

interface OnInviteClicked {
    fun onInviteClicked(user: User, position:Int)
}