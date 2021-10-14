package com.some_package.nbaquiz.ui.match

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomLoading
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WaitingFragment : Fragment(R.layout.fragment_waiting) {

    private  val TAG = "WaitingFragment"

    private lateinit var yourAvatarIV: ImageView
    private lateinit var rivalAvatarIV: ImageView
    private lateinit var yourNameTV: TextView
    private lateinit var rivalNameTV: TextView
    private lateinit var cancelBTN: Button
    private lateinit var progress: CustomLoading
    private lateinit var preparing: LinearLayout
    private lateinit var nextBtn:Button

    ///
    private val questionsList:ArrayList<Question> = ArrayList()
    private lateinit var roomId:String
    private var myRole:Int? = null
    private val rivalInfo = HashMap<String,Any?>()
    ///

    private val viewModel:MatchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeMyInfo()
        observeMyStatus()
        setImBusy()

        observeJoin()
        observeQuestionsStatus()
        observeCreateRoom()
        player2Founded()
        observeRivalInfo()
        observeStatingGameStatus()
        setupCancelButton()
        findRival()
        setupStartMatchButton()

    }


    private fun init(view: View){
        yourAvatarIV = view.findViewById(R.id.iv_avatar_you_waiting_fragment)
        yourNameTV = view.findViewById(R.id.tv_username_you_waiting_fragment)
        rivalAvatarIV = view.findViewById(R.id.iv_avatar_rival_waiting_fragment)
        rivalNameTV = view.findViewById(R.id.tv_username_rival_waiting_fragment)
        cancelBTN = view.findViewById(R.id.btn_cancel_waiting_fragment)
        nextBtn = view.findViewById(R.id.btn_next_waiting_fragment)
        progress = view.findViewById(R.id.progress_waiting_fragment)
        preparing = view.findViewById(R.id.ll_preparing)

        viewModel.initUserData()




    }

    private fun setupStartMatchButton(){
        nextBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("roomId",roomId)
            bundle.putInt("myRole",myRole!!)
            bundle.putString("username",rivalInfo["username"] as String)
            bundle.putInt("avatar",rivalInfo["avatar"] as Int)
            bundle.putInt("team",rivalInfo["team"] as Int)
            Log.i(TAG, "startMatch: start shod")
            Navigation.findNavController(nextBtn).navigate(R.id.action_waitingFragment_to_matchFragment,bundle)
        }
    }

    private fun setImBusy(){
        Log.i(TAG, "setImBusy: start fo set")
        viewModel.setImBusy()
    }
    private fun findRival(){
        Log.i(TAG, "findRival: join start")
        viewModel.joinRoom()
    }
    private fun createRoom(){
        Log.i(TAG, "createRoom: create start")
        viewModel.createRoom()
    }
    private fun notifyHeyIGotQuestions(){
        viewModel.changeStartingGameStatus(roomId)
    }
    private fun observeP2(){
        viewModel.observeP2(roomId)
    }
    private fun getRivalInfo(){
        val role = when(myRole){
            FirebaseProvider.HOST ->{
                "P2"
            }
            FirebaseProvider.GUEST ->{
                "P1"
            }
            else -> ""
        }
        viewModel.getRivalInfo(roomId,role)
    }


    private fun observeMyInfo(){
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            yourNameTV.text = it!!.username
            yourAvatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(), StaticHolder.avatars[it.avatar!!]))
        })
    }
    private fun observeMyStatus(){
        viewModel.dataStateMyStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    Log.i(TAG, "observeMyStatus: set shod")
                    // go ahead ::: run methods
                }
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    //back to find fragment you cant start a game
                }
            }
        })
    }
    //here
    private fun observeJoin(){
        viewModel.dataStateJoiningRoom.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Loading -> {
                    progress.visibility = View.VISIBLE
                }
                is DataState.Success -> {
                    // join
                    // i have room id
                    roomId = it.data!!
                    // i am P2
                    myRole = FirebaseProvider.GUEST
                    //get P1 details
                    getRivalInfo()
                    //go for get Questions
                    viewModel.observeQuestionsAddingStatus(it.data)
                    Log.i(TAG, "observeJoin: joined")
                }
                is DataState.Error -> {
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
                    notifyHeyIGotQuestions()
                }
            }
        })
    }
    private fun observeStatingGameStatus(){
        viewModel.dataStateStartingStatus.observe(viewLifecycleOwner, Observer {
            when (it){
                is DataState.Success ->{
                    // P1 & P2
                    // start game ::: we have room Id and questions

                    startMatch()

                }
            }
        })
    }
    private fun observeCreateRoom(){
        viewModel.dataStateCreationRoom.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Loading -> {
                    progress.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    //fuck
                    Log.i(TAG, "observeCreateRoom: error")
                }
                is DataState.Success -> {
                    // i have room id
                    roomId = it.data!!
                    // i am P1
                    myRole = FirebaseProvider.HOST

                    observeP2()
                }
            }
        })
    }
    //here
    private fun player2Founded(){
        viewModel.dataStateObservePlayer2.observe(viewLifecycleOwner, Observer {
            if (it is DataState.Success){
                // get P2 details
                viewModel.getRandomQuestionsFromFireStore(roomId)
                getRivalInfo()
            }
        })
    }

    private fun observeRivalInfo(){
        viewModel.dataStateRivalInfo.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    // set info
                    rivalInfo.putAll(it.data!!)
                    rivalNameTV.text = it.data["username"] as String
                    rivalAvatarIV.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(),
                            StaticHolder.avatars[it.data["avatar"] as Int]))
                    progress.visibility = View.GONE
                    rivalNameTV.visibility = View.VISIBLE
                    rivalAvatarIV.visibility = View.VISIBLE
                    preparing.visibility = View.VISIBLE
                }
                is DataState.Error ->{

                }
                is DataState.Loading ->{

                }
            }
        })
    }

    private fun setupCancelButton(){
        cancelBTN.setOnClickListener {

        }
    }

    private fun startMatch(){
        nextBtn.callOnClick()
    }

}