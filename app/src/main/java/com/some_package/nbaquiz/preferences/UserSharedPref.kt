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

    fun updateUserPref(team: Int?, avatar: Int?) {
        if (avatar == null && team == null) return
        val editor: Editor = sharedPreferences.edit()
        if (avatar !=null) editor.putInt("avatar", avatar)
        if (team != null) editor.putInt("team", team)
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

    fun incrementGame():Int{
        val editor = sharedPreferences.edit()
        val previousGameCount = sharedPreferences.getInt("games",-1)
        val currentGameCount = previousGameCount+1
        editor.putInt("games",currentGameCount)
        editor.apply()
        return currentGameCount
    }

    fun incrementWin():Int{
        val editor = sharedPreferences.edit()
        val previousWinCount = sharedPreferences.getInt("wins",-1)
        val currentWinCount = previousWinCount+1
        editor.putInt("wins",currentWinCount)
        editor.apply()
        return currentWinCount
    }

    fun incrementPoint(point:Int):Int{
        val editor = sharedPreferences.edit()
        val previousPointCount = sharedPreferences.getInt("points",-1)
        val currentPointCount = previousPointCount+point
        editor.putInt("points",currentPointCount)
        editor.apply()
        return currentPointCount
    }



}