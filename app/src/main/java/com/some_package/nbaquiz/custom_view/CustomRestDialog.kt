package com.some_package.nbaquiz.custom_view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.some_package.nbaquiz.R

class CustomRestDialog(context: Context) : Dialog(context) {

    init {
        setContentView(R.layout.layout_dialog_rest)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}