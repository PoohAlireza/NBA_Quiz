package com.some_package.nbaquiz

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application(){
    companion object {

        //fonts
        private var questrial: Typeface? = null
        private var quicksand: Typeface? = null
        private var raleway: Typeface? = null
        private var bernard: Typeface? = null

        fun getBernard(context: Context): Typeface? {
            if (bernard == null) {
                bernard = Typeface.createFromAsset(context.assets, "fonts/bernard.ttf")
            }
            return bernard
        }
        fun getQuicksand(context: Context): Typeface? {
            if (quicksand == null) {
                quicksand = Typeface.createFromAsset(context.assets, "fonts/quicksand_bold.ttf")
            }
            return quicksand
        }

        fun getRaleway(context: Context): Typeface? {
            if (raleway == null) {
                raleway = Typeface.createFromAsset(context.assets, "fonts/raleway_italic.ttf")
            }
            return raleway
        }

        fun getQuestrial(context: Context): Typeface? {
            if (questrial == null) {
                questrial = Typeface.createFromAsset(context.assets, "fonts/questrial.ttf")
            }
            return questrial
        }
    }




}