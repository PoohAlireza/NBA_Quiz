package com.some_package.nbaquiz.ui.match

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomRestDialog
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MatchFragment : Fragment(R.layout.fragment_match) , View.OnClickListener{
    private val TAG = "MatchFragment"

    private val QUARTER_1 = 0
    private val QUARTER_2 = 3
    private val QUARTER_3 = 6
    private val QUARTER_4 = 9
    private val END_GAME = 12

    private lateinit var yourAvatarIV: ImageView
    private lateinit var rivalAvatarIV:ImageView
    private lateinit var yourTeamIV:ImageView
    private lateinit var rivalTeamIV:ImageView
    private lateinit var yourFirstAnswerStateIV:ImageView
    private lateinit var yourSecondAnswerStateIV:ImageView
    private lateinit var yourThirdAnswerStateIV:ImageView
    private lateinit var rivalFirstAnswerStateIV:ImageView
    private lateinit var rivalSecondAnswerStateIV:ImageView
    private lateinit var rivalThirdAnswerStateIV:ImageView
    private lateinit var firstQuarterIV:ImageView
    private lateinit var secondQuarterIV:ImageView
    private lateinit var thirdQuarterIV:ImageView
    private lateinit var fourthQuarterIV:ImageView
    private lateinit var yourUsernameTV:TextView
    private lateinit var rivalUsernameTV:TextView
    private lateinit var yourPointsTV:TextView
    private lateinit var rivalPointsTV:TextView
    private lateinit var timerTV:TextView
    private lateinit var firstAnswerTV:TextView
    private lateinit var secondAnswerTV:TextView
    private lateinit var thirdAnswerTV:TextView
    private lateinit var fourthAnswerTV:TextView
    private lateinit var questionTV:TextView
    private lateinit var questionNumberTV:TextView
    private lateinit var questionKindTV:TextView
    private lateinit var progressTimer:ProgressBar

    private val optionButtons:ArrayList<TextView> = ArrayList()
    private lateinit var roomId:String
    private lateinit var myRole:String
    private lateinit var rivalRole:String
    private val questionsList:ArrayList<Question> = ArrayList()
    private var yourQuestionNumber:Int = 0
    private var rivalQuestionNumber:Int = 0
    private var timeCounter = 30
    private lateinit var colorAnimation: ValueAnimator
    private lateinit var scaleAnimationStart: ScaleAnimation
    private lateinit var timer: Timer
    private lateinit var timerTask:MyTimer
    private lateinit var restDialog: CustomRestDialog
    private var clickAllowed:Boolean = true

    private val viewModel:MatchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        firstSetup()
        callAllObserve()
        setTimerAnim()
        startTimer()
        setupNext()
    }

    private fun init(view: View){
        yourAvatarIV = view.findViewById(R.id.iv_avatar_you_match)
        rivalAvatarIV = view.findViewById(R.id.iv_avatar_rival_match)
        yourTeamIV = view.findViewById(R.id.iv_team_you_match)
        rivalTeamIV = view.findViewById(R.id.iv_team_rival_match)
        yourFirstAnswerStateIV = view.findViewById(R.id.iv_state_answer1_you_match)
        yourSecondAnswerStateIV = view.findViewById(R.id.iv_state_answer2_you_match)
        yourThirdAnswerStateIV = view.findViewById(R.id.iv_state_answer3_you_match)
        rivalFirstAnswerStateIV = view.findViewById(R.id.iv_state_answer1_rival_match)
        rivalSecondAnswerStateIV = view.findViewById(R.id.iv_state_answer2_rival_match)
        rivalThirdAnswerStateIV = view.findViewById(R.id.iv_state_answer3_rival_match)
        firstQuarterIV = view.findViewById(R.id.iv_ball_quarter1_match)
        secondQuarterIV = view.findViewById(R.id.iv_ball_quarter2_match)
        thirdQuarterIV = view.findViewById(R.id.iv_ball_quarter3_match)
        fourthQuarterIV = view.findViewById(R.id.iv_ball_quarter4_match)
        yourUsernameTV = view.findViewById(R.id.tv_username_you_match)
        rivalUsernameTV = view.findViewById(R.id.tv_username_rival_match)
        yourPointsTV = view.findViewById(R.id.tv_points_you_match)
        rivalPointsTV = view.findViewById(R.id.tv_points_rival_match)
        timerTV = view.findViewById(R.id.tv_timer_match)
        firstAnswerTV = view.findViewById(R.id.tv_answer1)
        secondAnswerTV = view.findViewById(R.id.tv_answer2)
        thirdAnswerTV = view.findViewById(R.id.tv_answer3)
        fourthAnswerTV = view.findViewById(R.id.tv_answer4)
        firstAnswerTV.setOnClickListener(this)
        secondAnswerTV.setOnClickListener(this)
        thirdAnswerTV.setOnClickListener(this)
        fourthAnswerTV.setOnClickListener(this)
        questionTV = view.findViewById(R.id.tv_question_match)
        questionNumberTV = view.findViewById(R.id.tv_question_number)
        questionKindTV = view.findViewById(R.id.tv_point_kind_match)
        progressTimer = view.findViewById(R.id.progress_timer_match)
        optionButtons.add(firstAnswerTV)
        optionButtons.add(secondAnswerTV)
        optionButtons.add(thirdAnswerTV)
        optionButtons.add(fourthAnswerTV)
        progressTimer.max = 300
        restDialog = CustomRestDialog(requireContext())
    }

    private fun firstSetup(){
        viewModel.initUserData()
        val bundle = requireArguments()
        Log.i(TAG, "firstSetup: $bundle")
        rivalUsernameTV.text = bundle.getString("username")
        rivalTeamIV.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), StaticHolder.team_logo[bundle.getInt(
                    "team"
                )]
            )
        )
        rivalAvatarIV.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), StaticHolder.avatars[bundle.getInt(
                    "avatar"
                )]
            )
        )
        questionsList.addAll(bundle.getParcelableArrayList<Question>("questions")!!)
        roomId = bundle.getString("roomId")!!
        when(bundle.getInt("myRole")){
            FirebaseProvider.HOST -> {
                myRole = "P1"
                rivalRole = "P2"
            }
            FirebaseProvider.GUEST -> {
                myRole = "P2"
                rivalRole = "P1"
            }
            else -> ""
        }


    }

    private fun setupNext(){
        setQuestionText(questionsList[yourQuestionNumber])
        setOptions(questionsList[yourQuestionNumber])
        setupQuarter(yourQuestionNumber)
        setQuestionKind(yourQuestionNumber)
        setQuestionNumber(yourQuestionNumber)
    }
    private fun setQuestionKind(number: Int){
        questionKindTV.text = when (number){
            0,1,2-> "Free throw (1P)"
            3,4,5 -> "Mid range (2P)"
            6,7,8 -> "3 Pointer"
            9,10,11 -> "4 Point play"
            else -> "Point"
        }
    }
    private fun setupQuarter(number: Int){
        when (number){
            QUARTER_1 -> {
                firstQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

            }
            QUARTER_2 -> {
                firstQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darker_grey
                    )
                )
                secondQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
            QUARTER_3 -> {
                secondQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darker_grey
                    )
                )
                thirdQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
            QUARTER_4 -> {
                thirdQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darker_grey
                    )
                )
                fourthQuarterIV.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }
    }
    private fun resetAnswersState(){
        yourFirstAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
        yourSecondAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
        yourThirdAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
        rivalFirstAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
        rivalSecondAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
        rivalThirdAnswerStateIV.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.gray,
                null
            )
        )
    }
    private fun setQuestionText(question: Question){
        questionTV.text = question.question
    }
    private fun setOptions(question: Question){
        firstAnswerTV.text = question.options!![0].choice
        secondAnswerTV.text = question.options!![1].choice
        thirdAnswerTV.text = question.options!![2].choice
        fourthAnswerTV.text = question.options!![3].choice
        firstAnswerTV.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        secondAnswerTV.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        thirdAnswerTV.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        fourthAnswerTV.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
    }
    private fun setQuestionNumber(number: Int){
        questionNumberTV.text = "${number+1}/12"
    }

    ///// observe
    private fun callAllObserve(){
        observeMyInfo()
        observeAnswer()
        observeQuarter()
        observeRivalPoint()
    }
    private fun observeMyInfo(){
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            yourUsernameTV.text = it.username
            yourTeamIV.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    StaticHolder.team_logo[it.team!!]
                )
            )
            yourAvatarIV.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    StaticHolder.avatars[it.avatar!!]
                )
            )
        })
    }
    private fun observeQuarter(){
        viewModel.dataStateQuarter.observe(viewLifecycleOwner, Observer {
            if (it is DataState.Success) {
                when (it.data) {
                    //start quarter 2,3,4
                    2,4,6 -> {
                        if (restDialog.isShowing){
                            restDialog.dismiss()
                            startTimer()
                            clickAllowed = true
                            resetAnswersState()
                        }
                    }
                    //game ended
                    8 -> {
                        if (restDialog.isShowing){
                            restDialog.dismiss()
                            // go to another frag or show new dialog
                            endGame()
                        }
                    }
                }
            }
        })
        viewModel.observeQuarter(roomId)
    }
    private fun observeRivalPoint(){
        viewModel.dataStatePoint.observe(viewLifecycleOwner, Observer {
            if (it is DataState.Success) {
                rivalPointsTV.text = "${it.data!!}"
            }
        })

        viewModel.observePoint(roomId,rivalRole)
    }
    private fun observeAnswer(){
        viewModel.dataStateAnswer.observe(viewLifecycleOwner, Observer {
            if (it is DataState.Success) {
                val answerIV:ImageView? = when(rivalQuestionNumber%3){
                    0 ->{
                        rivalFirstAnswerStateIV
                    }
                    1 ->{
                        rivalSecondAnswerStateIV
                    }
                    2 ->{
                        rivalThirdAnswerStateIV
                    }
                    else -> null
                }
                when (it.data) {
                    FirebaseProvider.ANSWER_CORRECT -> {
                        answerIV!!.setColorFilter(ResourcesCompat.getColor(resources,R.color.green,null))
                        rivalQuestionNumber++
                    }
                    FirebaseProvider.ANSWER_WRONG -> {
                        answerIV!!.setColorFilter(ResourcesCompat.getColor(resources,R.color.red,null))
                        rivalQuestionNumber++
                    }
                    FirebaseProvider.ANSWER_EMPTY -> {
                        // nothing
                    }
                    FirebaseProvider.ANSWER_NO_TIME -> {
                        when(rivalQuestionNumber){
                            0,1,2 -> rivalQuestionNumber = QUARTER_2
                            3,4,5 -> rivalQuestionNumber = QUARTER_3
                            6,7,8 -> rivalQuestionNumber = QUARTER_4
                            9,10,11 -> rivalQuestionNumber = END_GAME
                        }
                    }
                }
            }
        })
        viewModel.observeAnswer(roomId,rivalRole)
    }
    /////

    ////setDataToFirebase
    private fun setPoint(){
        var point = 0
        when (yourQuestionNumber){
            0,1,2 -> point = 1
            3,4,5 -> point = 2
            6,7,8 -> point = 3
            9,10,11 -> point = 4
        }
        viewModel.setPoint(point, roomId, myRole)
        yourPointsTV.text = "${yourPointsTV.text.toString().toInt()+point}"
    }
    private fun setAnswer(answer: Int, state: Boolean){
        //online
        viewModel.setAnswer(answer, roomId, myRole)
        //offline
        if (answer == FirebaseProvider.ANSWER_NO_TIME) return
        when(yourQuestionNumber%3){
            0 -> {
                if (state) yourFirstAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                else yourFirstAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            }
            1 -> {
                if (state) yourSecondAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                else yourSecondAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            }
            2 -> {
                if (state) yourThirdAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    )
                )
                else yourThirdAnswerStateIV.setColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.red,
                        null
                    )
                )
            }
        }
    }
    private fun notifyFinishCurrentQuarter(){
        viewModel.setQuarter(roomId)
    }
    private fun addTime(time: Int){
        viewModel.addTime(roomId, time, myRole)
    }
    ////

    private fun checkQuarterFinishing(){
        if (yourQuestionNumber == QUARTER_2-1 || yourQuestionNumber == QUARTER_3-1 || yourQuestionNumber == QUARTER_4-1 || yourQuestionNumber == END_GAME-1){
            addTime(timeCounter)
            resetTimer()
            restDialog.show()
            notifyFinishCurrentQuarter()
        }
    }
    private fun timeFinished(){
        restDialog.show()
        when(yourQuestionNumber){
            0,1,2 -> yourQuestionNumber = QUARTER_2
            3,4,5 -> yourQuestionNumber = QUARTER_3
            6,7,8 -> yourQuestionNumber = QUARTER_4
            9,10,11 -> yourQuestionNumber = END_GAME
        }
        resetTimer()
        setAnswer(FirebaseProvider.ANSWER_NO_TIME,false)
        notifyFinishCurrentQuarter()
    }
    private fun endGame(){

    }
    private fun judgeAnswer(answer: Int){
        //correct
        if (answer == questionsList[yourQuestionNumber].answer){
            optionButtons[answer-1].setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.green,
                    null
                )
            )
            setAnswer(FirebaseProvider.ANSWER_CORRECT, true)
            setPoint()
        }
        //wrong
        else{
            optionButtons[answer-1].setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.red,
                    null
                )
            )
            optionButtons[questionsList[yourQuestionNumber].answer!!-1].setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.green,
                    null
                )
            )
            setAnswer(FirebaseProvider.ANSWER_WRONG, false)
        }
        checkQuarterFinishing()
        yourQuestionNumber++
        if (yourQuestionNumber != END_GAME) setupNext()

    }

    private fun setTimerAnim() {
        colorAnimation = ValueAnimator.ofObject(
            ArgbEvaluator(), ResourcesCompat.getColor(
                resources,
                R.color.green,
                null
            ), ResourcesCompat.getColor(resources, R.color.red, null)
        )
        colorAnimation.duration = 10000 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            val layerDrawable = progressTimer.progressDrawable as LayerDrawable
            layerDrawable.getDrawable(1).setColorFilter(
                (animator.animatedValue as Int),
                PorterDuff.Mode.SRC_IN
            )
        }
        scaleAnimationStart = ScaleAnimation(
            1f,
            1.1f,
            1f,
            1.1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimationStart.duration = 250
        val scaleAnimationEnd = ScaleAnimation(
            1.1f,
            1f,
            1.1f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimationEnd.duration = 250
        scaleAnimationStart.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (progressTimer.progress in 51..299) progressTimer.startAnimation(
                    scaleAnimationEnd
                )
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        scaleAnimationEnd.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (progressTimer.progress in 51..299) progressTimer.startAnimation(
                    scaleAnimationStart
                )
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    override fun onClick(v: View?) {
        if (clickAllowed){
            when(v!!.id){
                firstAnswerTV.id -> {
                    judgeAnswer(1)
                }
                secondAnswerTV.id -> {
                    judgeAnswer(2)
                }
                thirdAnswerTV.id -> {
                    judgeAnswer(3)
                }
                fourthAnswerTV.id -> {
                    judgeAnswer(4)
                }
            }
        }
    }

    private fun startTimer(){
        timer = Timer()
        timerTask = MyTimer()
        timer.schedule(timerTask, 0, 100)
    }
    private fun resetTimer(){
        progressTimer.progress = 0
        timeCounter = 30
        timerTV.text = "$timeCounter"
        val layerDrawable = progressTimer.progressDrawable as LayerDrawable
        layerDrawable.getDrawable(1).setColorFilter(
            (ResourcesCompat.getColor(
                resources,
                R.color.green,
                null
            )), PorterDuff.Mode.SRC_IN
        )
        timer.cancel()
    }


    inner class MyTimer : TimerTask() {
        override fun run() {
            requireActivity().runOnUiThread(Runnable {
                if (progressTimer.progress == 295){
                    clickAllowed = false
                }
                if (progressTimer.progress == 300) {
                    timeFinished()
                    if (yourQuestionNumber != END_GAME) setupNext()
                }
                if (progressTimer.progress == 200) {
                    colorAnimation.start()
                    progressTimer.startAnimation(scaleAnimationStart)
                }

                val newP: Int = progressTimer.progress + 1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressTimer.setProgress(newP, true)
                } else {
                    progressTimer.progress = newP
                }
                if (progressTimer.progress % 10 == 0 && progressTimer.progress != 0) {
                    timerTV.text = "${(--timeCounter)}"
                }
            })
        }
    }

}