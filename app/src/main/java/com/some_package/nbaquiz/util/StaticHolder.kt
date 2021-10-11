package com.some_package.nbaquiz.util

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnGameKindPageSelected
import com.some_package.nbaquiz.model.Option
import com.some_package.nbaquiz.model.Question
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class StaticHolder {



//    var onEditProfileClicked: OnEditProfileClicked? = null
    companion object{
        val KIND_FIND_RIVAL = 100

        var onGameKindPageSelected: OnGameKindPageSelected? = null



        fun fullScreen(activity: Activity?) {
            if (activity == null) return
            val window = activity.window
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
            window.decorView.systemUiVisibility = uiOptions
        }

        val teams_name = arrayOf(
            "ATL",
            "BKN",
            "BOS",
            "CHA",
            "CHI",
            "CLE",
            "DAL",
            "DEN",
            "DET",
            "GSW",
            "HOU",
            "IND",
            "LAC",
            "LAL",
            "MEM",
            "MIA",
            "MIL",
            "MIN",
            "NOP",
            "NYK",
            "OKC",
            "ORL",
            "PHI",
            "PHX",
            "POR",
            "SAC",
            "SAS",
            "TOR",
            "UTA",
            "WAS"
        )


        val team_logo = intArrayOf(
            R.drawable.logo_atl,
            R.drawable.logo_bkn,
            R.drawable.logo_bos,
            R.drawable.logo_cha,
            R.drawable.logo_chi,
            R.drawable.logo_cle,
            R.drawable.logo_dal,
            R.drawable.logo_den,
            R.drawable.logo_det,
            R.drawable.logo_gsw,
            R.drawable.logo_hou,
            R.drawable.logo_ind,
            R.drawable.logo_lac,
            R.drawable.logo_lal,
            R.drawable.logo_mem,
            R.drawable.logo_mia,
            R.drawable.logo_mil,
            R.drawable.logo_min,
            R.drawable.logo_nop,
            R.drawable.logo_nyk,
            R.drawable.logo_okc,
            R.drawable.logo_orl,
            R.drawable.logo_phi,
            R.drawable.logo_phx,
            R.drawable.logo_por,
            R.drawable.logo_sac,
            R.drawable.logo_sas,
            R.drawable.logo_tor,
            R.drawable.logo_uta,
            R.drawable.logo_was
        )

        val avatars = intArrayOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
            R.drawable.avatar_7, R.drawable.avatar_8, R.drawable.avatar_9,
            R.drawable.avatar_10, R.drawable.avatar_11, R.drawable.avatar_12
        )


    fun addQuestionForAdmin(jsonArray: String?) {
        try {
            val jsonObject = JSONObject(jsonArray!!)
            val array = jsonObject.getJSONArray("questions")
            val cf = FirebaseFirestore.getInstance().collection("questions")
            ////////////////
            for (i in 0 until array.length()) {
                val question = Question()
                val index = array.getJSONObject(i)
                val options = index.getJSONArray("options")
                val optionList: ArrayList<Option> = ArrayList()
                for (j in 0 until options.length()) {
                    val option = Option()
                    option.id = j + 1
                    option.choice = options.getJSONObject(j).getString("choice")
                    optionList.add(option)
                }
                question.options = optionList
                question.id = index.getString("id").toInt()
                question.answer = index.getString("answer").toInt()
                question.question = index.getString("question")

                cf.add(question).addOnSuccessListener {
                    Log.i("admin", "addQuestionForAdmin: question added ! ")
                }.addOnFailureListener {
                    Log.i("admin", "addQuestionForAdmin: question not added!!!!!!!!!!!!!")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    }




}