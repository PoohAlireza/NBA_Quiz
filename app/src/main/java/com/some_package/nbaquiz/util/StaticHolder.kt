package com.some_package.nbaquiz.util

import android.app.Activity
import android.view.View
import android.view.WindowManager
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnGameKindPageSelected

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

    }




}