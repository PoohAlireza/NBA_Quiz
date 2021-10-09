package com.some_package.nbaquiz.preferences

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.some_package.nbaquiz.di.PrefModule
import com.some_package.nbaquiz.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSharedPref @Inject constructor(@PrefModule.UserPref private val sharedPreferences: SharedPreferences) {

    companion object{
        val USER_PREF:String = "user_pref"
    }


    fun setUserPref(user: User) {
        val editor: Editor = sharedPreferences.edit()
        editor.putString("id", user.id)
        editor.putString("username", user.username)
        editor.putInt("avatar", user.avatar!!)
        editor.putInt("team", user.team!!)
        editor.putInt("points", user.points!!)
        editor.putInt("wins", user.wins!!)
        editor.putInt("games", user.games!!)
        editor.putString("lowerCaseUsername", user.lowerCaseUsername)
        editor.apply()
    }

    fun updateUserPref(team: Int, avatar: Int) {
        val editor: Editor = sharedPreferences.edit()
        editor.putInt("avatar", avatar)
        editor.putInt("team", team)
        editor.apply()
    }

    fun getUserPref(): User {
        val user = User()
        user.id = sharedPreferences.getString("id", "")
        user.avatar = sharedPreferences.getInt("avatar", 0)
        user.team = sharedPreferences.getInt("team", 0)
        user.username = sharedPreferences.getString("username", "")
        user.games = sharedPreferences.getInt("games", 0)
        user.wins = sharedPreferences.getInt("wins", 0)
        user.points = sharedPreferences.getInt("points", 0)
        user.lowerCaseUsername = sharedPreferences.getString("lowerCaseUsername", "")
        return user
    }


}