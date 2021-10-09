package com.some_package.nbaquiz.custom_view
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import com.some_package.nbaquiz.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomLoading : LinearLayout {

    private lateinit var mRootView:View
    private lateinit var ball1:ImageView
    private lateinit var ball2:ImageView
    private lateinit var ball3:ImageView
    private lateinit var balls:Array<View>
    private var currentIndex = 0
    private var leftToRight:Boolean = true

    constructor(context: Context):super(context){
        init()
    }
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        init()
    }

    private fun init(){
        mRootView = inflate(context, R.layout.layout_custom_loading,this)
        ball1 = mRootView.findViewById(R.id.iv_ball1_loading)
        ball2 = mRootView.findViewById(R.id.iv_ball2_loading)
        ball3 = mRootView.findViewById(R.id.iv_ball3_loading)
        balls = arrayOf(ball1,ball2,ball3)

        start()

    }

    private fun start(){
        CoroutineScope(Main).launch {
           while (true){
               currentIndex = 0
               delay(250)
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(1f).duration = 500
               delay(300)
               currentIndex = if (currentIndex == 2) 0 else currentIndex+1
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(1f).duration = 500
               delay(300)
               currentIndex = if (currentIndex == 2) 0 else currentIndex+1
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(1f).duration = 500

               switchDirection()

               delay(1000)
               currentIndex = if (currentIndex == 2) 0 else currentIndex+1
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(0f).duration = 500
               delay(300)
               currentIndex = if (currentIndex == 2) 0 else currentIndex+1
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(0f).duration = 500
               delay(300)
               currentIndex = if (currentIndex == 2) 0 else currentIndex+1
               balls[currentIndex].animate().setInterpolator(AccelerateInterpolator()).alpha(0f).duration = 500
           }
        }
    }

    private fun switchDirection(){
        leftToRight = !leftToRight
        balls = if (leftToRight) arrayOf(ball1,ball2,ball3)
        else arrayOf(ball3,ball2,ball1)
    }




}