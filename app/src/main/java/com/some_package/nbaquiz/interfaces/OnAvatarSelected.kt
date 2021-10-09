package com.some_package.nbaquiz.interfaces

import android.graphics.drawable.Drawable

interface OnAvatarSelected {
    fun onSelected(image:Drawable?,index:Int)
}