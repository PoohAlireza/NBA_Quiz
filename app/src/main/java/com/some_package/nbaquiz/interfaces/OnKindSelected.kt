package com.some_package.nbaquiz.interfaces

import android.view.View

interface OnKindSelected {
    fun onSelected(kind:Int,layoutId:Int,clickedItem:View)
}