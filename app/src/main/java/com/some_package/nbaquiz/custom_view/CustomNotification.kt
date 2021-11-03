package com.some_package.nbaquiz.custom_view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnInviteAnswer
import com.some_package.nbaquiz.util.StaticHolder
import java.lang.Exception
import kotlin.math.log

class CustomNotification(
    private val context: Context,
    private val username: String,
    private val team: Int,
    private val avatar: Int
) : View.OnClickListener {

    private lateinit var dialog: Dialog
    private lateinit var acceptBTN: Button
    private lateinit var declineBTN: Button
    private lateinit var usernameTV: TextView
    private lateinit var teamTV: TextView
    private lateinit var avatarIV: ImageView
    private lateinit var listener: OnInviteAnswer

    private fun init(){
        acceptBTN = dialog.findViewById(R.id.btn_accept_notification)
        declineBTN = dialog.findViewById(R.id.btn_decline_notification)
        usernameTV = dialog.findViewById(R.id.tv_username_notification)
        avatarIV = dialog.findViewById(R.id.iv_avatar_notification)
        teamTV = dialog.findViewById(R.id.tv_team_notification)

        usernameTV.text = username
        teamTV.text = StaticHolder.teams_name[team]
        avatarIV.setImageDrawable(ContextCompat.getDrawable(context, StaticHolder.avatars[avatar]))

        acceptBTN.setOnClickListener(this)
        declineBTN.setOnClickListener(this)
    }

    fun show(listener: OnInviteAnswer){
        try {

            this.listener = listener
            dialog = Dialog(context, R.style.DialogSlideAnimRev)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.layout_notification)
            init()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val layoutParams:WindowManager.LayoutParams = WindowManager.LayoutParams()
            val window:Window = dialog.window!!
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
            dialog.window!!.setGravity(Gravity.TOP)

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

        }catch (e:Exception){
            Log.i("notif", "show: $e")
        }

    }

    fun dismiss(){
        dialog.dismiss()
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            acceptBTN.id -> {
                listener.onAccept()
            }
            declineBTN.id -> {
                listener.onDecline()
            }
        }
    }

}