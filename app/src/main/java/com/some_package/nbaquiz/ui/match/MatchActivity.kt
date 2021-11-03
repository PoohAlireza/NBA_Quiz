package com.some_package.nbaquiz.ui.match

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomNotification
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.interfaces.OnInviteAnswer
import com.some_package.nbaquiz.ui.main.MainViewModel
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MatchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        StaticHolder.fullScreen(this)

    }


}