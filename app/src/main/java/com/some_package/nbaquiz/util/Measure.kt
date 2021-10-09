package com.some_package.nbaquiz.util

import android.view.View

class Measure {

    companion object {

        fun getRelativeLeft(myView: View): Float {
            return if (myView.parent === myView.rootView) myView.left.toFloat() else myView.left + getRelativeLeft(myView.parent as View)
        }

        fun getRelativeTop(myView: View): Float {
            return if (myView.parent === myView.rootView) myView.top.toFloat() else myView.top + getRelativeTop(myView.parent as View)
        }

        fun getRelativeRight(myView: View): Float {
            return getRelativeLeft(myView) + myView.right - myView.left
        }

        fun getRelativeBottom(myView: View): Float {
            return getRelativeTop(myView) + myView.bottom - myView.top
        }


    }



}