package com.some_package.nbaquiz.ui.main

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomNotification
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.interfaces.OnGameKindPageSelected
import com.some_package.nbaquiz.interfaces.OnInviteAnswer
import com.some_package.nbaquiz.ui.match.MatchActivity
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.Measure
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val LEFT_PAGE = 0
    private val MIDDLE_PAGE = 1
    private val RIGHT_PAGE = 2
    private val FIND_RIVAL_PAGE = 3
    private var currentPosition = MIDDLE_PAGE

    private lateinit var leftBNV_IC: ImageView
    private lateinit var middleBNV_IC: ImageView
    private lateinit var rightBNV_IC: ImageView
    private lateinit var leftBNV_CL: ConstraintLayout
    private lateinit var middleBNV_CL: ConstraintLayout
    private lateinit var rightBNV_CL: ConstraintLayout
    private lateinit var lineView: View
    private lateinit var toolbarCL: ConstraintLayout
    private lateinit var toolbarNavComponent: Toolbar
    private lateinit var pointsTV: TextView
    private lateinit var gamesTV: TextView
    private lateinit var teamTV: TextView
    private lateinit var winsTV: TextView
    private lateinit var usernameTV: TextView
    private lateinit var avatarIV: ImageView
    private lateinit var teamBackgroundIV:ImageView

    private val viewModel:MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StaticHolder.fullScreen(this)
        init()
        setProfileInMiddle()
        setLineInMiddleOfBNV()
        observeUser()
        setupKindSelected()
        observeInvitation()

    }

    override fun onResume() {
        super.onResume()
        viewModel.initUserData()
        viewModel.observeInvitation()
    }

    override fun onStop() {
        super.onStop()

    }


    private fun init(){
        toolbarNavComponent = findViewById(R.id.nc_toolbar)
        toolbarNavComponent.title = ""
        setSupportActionBar(toolbarNavComponent)

        leftBNV_IC = findViewById(R.id.iv_left_bnv)
        middleBNV_IC = findViewById(R.id.iv_middle_bnv)
        rightBNV_IC = findViewById(R.id.iv_right_bnv)
        leftBNV_CL = findViewById(R.id.cl_left_bnv)
        middleBNV_CL = findViewById(R.id.cl_middle_bnv)
        rightBNV_CL = findViewById(R.id.cl_right_bnv)
        lineView = findViewById(R.id.v_line_main_activity)
        avatarIV = findViewById(R.id.iv_profile_main_activity)
        avatarIV.alpha = 0f
        toolbarCL = findViewById(R.id.cl_toolbar)
        pointsTV = findViewById(R.id.tv_points_main_activity)
        gamesTV = findViewById(R.id.tv_games_main_activity)
        winsTV = findViewById(R.id.tv_wins_main_activity)
        teamTV = findViewById(R.id.tv_team_main_activity)
        usernameTV = findViewById(R.id.tv_username_main_activity)
        teamBackgroundIV = findViewById(R.id.iv_team_background_main_activity)

        leftBNV_CL.setOnClickListener(this)
        middleBNV_CL.setOnClickListener(this)
        rightBNV_CL.setOnClickListener(this)



    }

    private fun setProfileInMiddle() {
        val observer = avatarIV.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                avatarIV.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val xCenterProfile = ((avatarIV.left + avatarIV.right) / 2).toFloat()
                val yCenterProfile = ((avatarIV.top + avatarIV.bottom) / 2).toFloat()
                val xCenterToolbar = (toolbarCL.width / 2).toFloat()
                val xPoint = xCenterToolbar - xCenterProfile
                val yPoint = toolbarCL.bottom - yCenterProfile
                avatarIV.animate().translationX(xPoint).translationY(yPoint).setDuration(1)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            avatarIV.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
            }
        })
    }

    private fun observeUser(){
        viewModel.userData.observe(this, Observer {
            usernameTV.text = it.username
            gamesTV.text = "${it.games}"
            pointsTV.text = "${it.points}"
            winsTV.text = "${it.wins}"
            teamTV.text = StaticHolder.teams_name[it.team!!]
            teamBackgroundIV.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    StaticHolder.team_logo[it.team!!],
                    null
                )
            )
            avatarIV.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    StaticHolder.avatars[it.avatar!!],
                    null
                )
            )
        })
    }

    private fun calculateLinePosition(cl: ConstraintLayout, icon: ImageView): FloatArray {
        val position = FloatArray(2)
        val lIcon: Float = Measure.getRelativeLeft(icon)
        val rIcon: Float = Measure.getRelativeRight(icon)
        val bIcon: Float = Measure.getRelativeBottom(icon)
        val bCL: Float = Measure.getRelativeBottom(cl)
        val xCenterLocation = (rIcon + lIcon) / 2
        val yCenterLocation = (bIcon + bCL) / 2
        val lBall: Float = Measure.getRelativeLeft(lineView)
        val tBall: Float = Measure.getRelativeTop(lineView)
        val rBall: Float = Measure.getRelativeRight(lineView)
        val bBall: Float = Measure.getRelativeBottom(lineView)
        val xCenterBall = (rBall + lBall) / 2
        val yCenterBall = (tBall + bBall) / 2
        val xPoint = xCenterLocation - xCenterBall
        val yPoint = yCenterLocation - yCenterBall
        position[0] = xPoint
        position[1] = yPoint
        return position
    }

    private fun setLineInMiddleOfBNV() {
        val observer = lineView.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                lineView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val position = calculateLinePosition(middleBNV_CL, middleBNV_IC)
                lineView.animate().translationX(position[0]).translationY(position[1])
                    .setDuration(1).start()
            }
        })
    }

    private fun runBNV(clicked: Int) {
        val position: FloatArray
        when (clicked) {

            LEFT_PAGE -> {
                position = calculateLinePosition(leftBNV_CL, leftBNV_IC)
                lineView.animate().translationX(position[0]).translationY(position[1]).setDuration(
                    100
                ).start()
                leftBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.orange))
                middleBNV_IC.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.white
                    )
                )
                rightBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
                if (currentPosition == MIDDLE_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_mainPageFragment_to_profileFragment)
                }
                if (currentPosition == RIGHT_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_rankingFragment_to_profileFragment)
                }
                if (currentPosition == FIND_RIVAL_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_findRivalFragment_to_profileFragment)
                }
                currentPosition = LEFT_PAGE
            }

            MIDDLE_PAGE -> {
                position = calculateLinePosition(middleBNV_CL, middleBNV_IC)
                lineView.animate().translationX(position[0]).translationY(position[1]).setDuration(
                    100
                ).start()
                middleBNV_IC.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.orange
                    )
                )
                leftBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
                rightBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
                if (currentPosition == LEFT_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_profileFragment_to_mainPageFragment)
                }
                if (currentPosition == RIGHT_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_rankingFragment_to_mainPageFragment)
                }
                if (currentPosition == FIND_RIVAL_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_findRivalFragment_to_mainPageFragment)
                }
                currentPosition = MIDDLE_PAGE
            }

            RIGHT_PAGE -> {
                position = calculateLinePosition(rightBNV_CL, rightBNV_IC)
                lineView.animate().translationX(position[0]).translationY(position[1]).setDuration(
                    100
                ).start()
                rightBNV_IC.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.orange
                    )
                )
                middleBNV_IC.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.white
                    )
                )
                leftBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
                if (currentPosition == LEFT_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_profileFragment_to_rankingFragment)
                }
                if (currentPosition == MIDDLE_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_mainPageFragment_to_rankingFragment)
                }
                if (currentPosition == FIND_RIVAL_PAGE) {
                    Navigation.findNavController(this@MainActivity, R.id.fragment)
                        .navigate(R.id.action_findRivalFragment_to_rankingFragment)
                }
                currentPosition = RIGHT_PAGE
            }

        }
    }

    private fun setupKindSelected(){
        StaticHolder.onGameKindPageSelected = object : OnGameKindPageSelected {
            override fun onSelected(kind: Int) {
                if (kind == StaticHolder.KIND_FIND_RIVAL) {
                    currentPosition = FIND_RIVAL_PAGE
                }
            }

        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == leftBNV_CL.id) {
            runBNV(LEFT_PAGE)
        }
        if (v.id == middleBNV_CL.id) {
            runBNV(MIDDLE_PAGE)
        }
        if (v.id == rightBNV_CL.id) {
            runBNV(RIGHT_PAGE)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        currentPosition = MIDDLE_PAGE
        val position: FloatArray = calculateLinePosition(middleBNV_CL, middleBNV_IC)
        lineView.animate().translationX(position[0]).translationY(position[1]).setDuration(100).start()
        middleBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.orange))
        leftBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
        rightBNV_IC.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white))
        StaticHolder.fullScreen(this)
    }

    private fun observeInvitation(){
        viewModel.dataStateMyInvitationState.observe(this, Observer {
            if (it is DataState.Success){
                val notification = CustomNotification(this,it.data!!.username!!,it.data.team!!,it.data.avatar!!)
                notification.show(object : OnInviteAnswer {
                    override fun onAccept() {
                        viewModel.answerToInvite(FirebaseProvider.INVITE_ANSWER_ACCEPT)
                        notification.dismiss()
                        val intent  = Intent(this@MainActivity, MatchActivity::class.java)
                        intent.putExtra("kind",StaticHolder.FRIENDLY_GUEST)
                        val map = HashMap<String,Any?>()
                        map["username"] = it.data.username
                        map["avatar"] = it.data.avatar
                        map["team"] = it.data.team
                        intent.putExtra("user",map)
                        startActivity(intent)
                    }

                    override fun onDecline() {
                        viewModel.answerToInvite(FirebaseProvider.INVITE_ANSWER_DECLINE)
                        notification.dismiss()
                    }

                })
            }
        })
    }

}