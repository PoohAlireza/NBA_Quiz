package com.some_package.nbaquiz.custom_view

import android.content.Context
import android.util.AttributeSet
import com.some_package.nbaquiz.MyApp

class CustomTextViewQuicksand : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context){
        if(!isInEditMode){
            setupTypeface()
        }
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        if(!isInEditMode){
            setupTypeface()
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        if(!isInEditMode){
            setupTypeface()
        }
    }


    private fun setupTypeface() {
        typeface = MyApp.getQuicksand(context)
    }

}