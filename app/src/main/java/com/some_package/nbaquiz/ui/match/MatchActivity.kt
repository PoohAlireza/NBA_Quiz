package com.some_package.nbaquiz.ui.match

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.ui.main.MainViewModel
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MatchActivity : AppCompatActivity() {


    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        StaticHolder.fullScreen(this)
        showInvite()
    }

    private fun showInvite(){
        viewModel.dataStateMyInvitationState.observe(this, Observer {
            if (it is DataState.Success){
                //show notif
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.observeInvitation()
    }

    override fun onStop() {
        super.onStop()

    }
}