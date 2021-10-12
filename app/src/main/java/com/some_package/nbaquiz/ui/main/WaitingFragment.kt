package com.some_package.nbaquiz.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomLoading
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WaitingFragment : Fragment(R.layout.fragment_waiting) {

    private lateinit var yourAvatarIV: ImageView
    private lateinit var rivalAvatarIV: ImageView
    private lateinit var yourNameTV: TextView
    private lateinit var rivalNameTV: TextView
    private lateinit var cancelBTN: Button
    private lateinit var progress:CustomLoading
    private lateinit var preparing:LinearLayout

    ///
    private val questionsList:ArrayList<Question> = ArrayList()
    private lateinit var roomId:String
    ///

    private val viewModel:MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeJoin()
        observeQuestionsStatus()
        observeCreateRoom()
        findRival()
    }

    private fun init(view: View){
        yourAvatarIV = view.findViewById(R.id.iv_avatar_you_waiting_fragment)
        yourNameTV = view.findViewById(R.id.tv_username_you_waiting_fragment)
        rivalAvatarIV = view.findViewById(R.id.iv_avatar_rival_waiting_fragment)
        rivalNameTV = view.findViewById(R.id.tv_username_rival_waiting_fragment)
        cancelBTN = view.findViewById(R.id.btn_cancel_waiting_fragment)
        progress = view.findViewById(R.id.progress_waiting_fragment)
        preparing = view.findViewById(R.id.ll_preparing)
    }

    private fun findRival(){
        viewModel.joinRoom()
    }
    private fun createRoom(){
        viewModel.createRoom()
    }
    private fun notifyHeyIGetQuestions(){
        viewModel.changeStartingGameStatus(roomId)
        // P2 is ready to Gooo
    }


    private fun observeJoin(){
        viewModel.dataStateJoiningRoom.observe(viewLifecycleOwner, Observer {
            when (it){
                is DataState.Loading ->{
                    progress.visibility = View.VISIBLE
                }
                is DataState.Success ->{
                    progress.visibility = View.GONE
                    preparing.visibility = View.VISIBLE
                    // join
                    // i have room id
                    roomId = it.data!!
                    // i am P2
                    viewModel.observeQuestionsAddingStatus(it.data)

                }
                is DataState.Error ->{
                    progress.visibility = View.VISIBLE
                    // create
                    createRoom()
                }
            }
        })
    }
    private fun observeQuestionsStatus(){
        viewModel.dataStateQuestions.observe(viewLifecycleOwner, Observer {
            when (it){
                is DataState.Success ->{
                    questionsList.addAll(it.data!!)
                    notifyHeyIGetQuestions()
                }
            }
        })
    }

    private fun observeCreateRoom(){
        viewModel.dataStateCreationRoom.observe(viewLifecycleOwner, Observer {
            when (it){
                is DataState.Loading ->{
                    progress.visibility = View.VISIBLE
                }
                is DataState.Error ->{
                    //fuck
                }
                is DataState.Success ->{
                    // i have room id
                    // i am P1
                }
            }
        })
    }

    private fun setupCancelButton(){

    }


}