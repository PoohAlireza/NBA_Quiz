package com.some_package.nbaquiz.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.di.AppModule
import com.some_package.nbaquiz.preferences.RegisterSharedPref
import com.some_package.nbaquiz.ui.main.MainActivity
import com.some_package.nbaquiz.ui.register.RegisterActivity
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var textTV: TextView
    private lateinit var authorTV: TextView
    private lateinit var textCardLL: LinearLayout
    private lateinit var hide: AnimationSet
    private lateinit var appear: AnimationSet
    private var index = 0

    @Inject @AppModule.Texts lateinit var texts:Array<String>
    @Inject @AppModule.Authors lateinit var authors:Array<String>
    @Inject lateinit var registerSharedPref: RegisterSharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        StaticHolder.fullScreen(this)
        init()
        initAnimCardView()
        startAnim()
        checkRegistering()

    }

    private fun init(){
        textTV = findViewById(R.id.tv_text_splash_activity)
        authorTV = findViewById(R.id.tv_author_splash_activity)
        textCardLL = findViewById(R.id.ll_splash)

        //set Texts
        index = Random.nextInt(3)
        textTV.text=texts[index]
        authorTV.text=authors[index]


    }

    private fun initAnimCardView() {
        hide = AnimationSet(true)
        hide.duration = 800
        hide.addAnimation(AlphaAnimation(1f, 0f))
        hide.addAnimation(ScaleAnimation(1f, .5f, 1f, .5f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f))

        appear = AnimationSet(true)
        appear.duration = 800
        appear.addAnimation(AlphaAnimation(0f, 1f))
        appear.addAnimation(ScaleAnimation(.5f, 1f, .5f, 1f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f))

        hide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val temp = java.util.Random().nextInt(3)
                index = if (temp != index) temp else if (temp + 1 > texts.size - 1) 0 else temp + 1
                textTV.text = texts[index]
                authorTV.text = authors[index]
                textCardLL.startAnimation(appear)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        appear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                Handler().postDelayed({ textCardLL.startAnimation(hide) }, 3500)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

    }
    private fun startAnim(){
        CoroutineScope(Main).launch {
            delay(4500)
            textCardLL.startAnimation(hide)
        }
    }


    private fun checkRegistering(){
        CoroutineScope(Main).launch {
            delay(6000)
            if (registerSharedPref.checkRegisterState()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
            }
            finish()
        }
    }




}