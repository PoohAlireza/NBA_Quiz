package com.some_package.nbaquiz.preferences

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.some_package.nbaquiz.di.PrefModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterSharedPref @Inject constructor(@PrefModule.RegisterPref private val sharedPreferences: SharedPreferences) {



    companion object{
        val REGISTER_PREF:String = "register_pref"

    }


    private val REGISTER_STATE:String = "register_state"


    fun register(register:Boolean){
        val editor: Editor = sharedPreferences.edit()
        editor.putBoolean(REGISTER_STATE,register)
        editor.apply()
    }

    fun checkRegisterState():Boolean{
        return sharedPreferences.getBoolean(REGISTER_STATE,false)
    }




}