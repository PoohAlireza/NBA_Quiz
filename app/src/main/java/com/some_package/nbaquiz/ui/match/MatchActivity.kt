package com.some_package.nbaquiz.ui.match

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        StaticHolder.fullScreen(this)
    }
}