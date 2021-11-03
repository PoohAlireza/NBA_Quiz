package com.some_package.nbaquiz.ui.match

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.CustomLoading
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
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
    private lateinit var warningTV:TextView

    ///
    private val questionsList:ArrayList<Question> = ArrayList()
    private lateinit var roomId:String
    private var myRole:Int? = null
    private val rivalInfo = HashMap<String,Any?>()
    ///


    private val viewModel:WaitingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeDataStateMyInfo()
        observeDataStateMyStatus()
        setImBusy()
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
        warningTV = view.findViewById(R.id.tv_warning_waiting_fragment)
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
            bundle.putParcelableArrayList("questions",questionsList)
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

    private fun runAsRandomly(){
        observeDataStateJoin()
        observeDataStateQuestionsStatus()
        observeDataStateCreateRoom()
        observeDataStatePlayer2Appearance()
        observeDataStateRivalInfo()
        observeDataStateStatingGameStatus()
        findRival()
        setupStartMatchButton()
    }
    private fun runAsHostFriendly(){
        val rivalId = requireArguments().getString("user_id")
        viewModel.sendInvite(rivalId!!)
        observeDataStateInvitation()
        observeDataStateInvitationAnswer()
        observeDataStateInvitationRoom()
        observeDataStateQuestionsStatus()
        observeDataStateStatingGameStatus()
        observeDataStatePlayer2Appearance()
        observeDataStateRivalInfo()
    }
    private fun runAsGuestFriendly(){
        val user = requireArguments().getParcelable<User>("user")
        rivalInfo["username"] = user!!.username
        rivalInfo["avatar"] = user.avatar
        rivalInfo["team"] = user.team
        rivalAvatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[user.avatar!!]))
        rivalNameTV.text = user.username
        progress.visibility = View.GONE
        rivalNameTV.visibility = View.VISIBLE
        rivalAvatarIV.visibility = View.VISIBLE
        preparing.visibility = View.VISIBLE
        observeDataStateJoinToInvitationRoom()
        observeDataStateQuestionsStatus()
        observeDataStateStatingGameStatus()
        viewModel.observeRoomId()
    }


    private fun observeDataStateMyInfo(){
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            yourNameTV.text = it!!.username
            yourAvatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(), StaticHolder.avatars[it.avatar!!]))
        })
    }
    private fun observeDataStateMyStatus(){
        viewModel.dataStateMyStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    // TODO: 28/10/2021 probably we add a new state for guest cuz methods of guest is different relative to host --> FriendlyGuest , FriendlyHost
                    when (requireActivity().intent.getIntExtra("kind", 0)){
                        StaticHolder.RANDOMLY -> runAsRandomly()
                        StaticHolder.FRIENDLY_HOST -> runAsHostFriendly()
                        StaticHolder.FRIENDLY_GUEST -> runAsGuestFriendly()
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    //error
                    requireActivity().finish()
                }
            }
        })
    }
    private fun observeDataStateJoin(){
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
                is DataState.Warning ->{

                }
            }
        })
    }
    private fun observeDataStateQuestionsStatus(){
        viewModel.dataStateQuestions.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    questionsList.addAll(it.data!!)
                    Log.i(TAG, "observeQuestionsStatus: $questionsList")
                    if(myRole == FirebaseProvider.GUEST) notifyHeyIGotQuestions()
                }
            }
        })
    }
    private fun observeDataStateStatingGameStatus(){
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
    private fun observeDataStateCreateRoom(){
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
    private fun observeDataStatePlayer2Appearance(){
        viewModel.dataStateObservePlayer2.observe(viewLifecycleOwner, Observer {
            if (it is DataState.Success){
                // get P2 details
                viewModel.getRandomQuestionsFromFireStore(roomId)
                getRivalInfo()
            }
        })
    }
    private fun observeDataStateRivalInfo(){
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

    private fun observeDataStateInvitation(){
        viewModel.dataStateSendInvitation.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Warning -> {
                    // TODO: 03/11/2021 back from this activity
                    warningTV.text = it.warning
                    progress.visibility = View.GONE
                    warningTV.visibility = View.VISIBLE
                }
                is DataState.Success -> {
                    viewModel.observeInvitationAnswer(requireArguments().getString("user_id")!!)
                }
                is DataState.Error -> {
                    Log.i(TAG, "observeDataStateInvitation: $it")
                }
                is DataState.Loading -> {
                    progress.visibility - View.VISIBLE
                }
            }
        })
    }
    private fun observeDataStateInvitationAnswer(){
        viewModel.dataStateInvitationAnswer.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    val answer = it.data
                    if (answer == FirebaseProvider.INVITE_ANSWER_ACCEPT) {
                        viewModel.createInvitationRoom()
                    } else {
                        // TODO: 03/11/2021 back from this activity
                        warningTV.text = "Your request rejected"
                        progress.visibility = View.GONE
                        warningTV.visibility = View.VISIBLE
                    }
                }
                is DataState.Error -> {
                    Log.i(TAG, "observeDataStateInvitationAnswer: $it")
                }
                is DataState.Loading -> {

                }
                is DataState.Warning -> {

                }
            }
        })
    }
    private fun observeDataStateInvitationRoom(){
        viewModel.dataStateInvitationRoom.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    roomId = it.data!!
                    myRole = FirebaseProvider.HOST
                    viewModel.setRoomIdForInvitedPlayer(requireArguments().getString("user_id")!!,roomId)
                    observeP2()
                }
                is DataState.Error -> {
                    Log.i(TAG, "observeDataStateInvitationAnswer: $it")
                }
                is DataState.Loading -> {

                }
                is DataState.Warning -> {

                }
            }
        })
    }
    private fun observeDataStateJoinToInvitationRoom(){
        viewModel.dataStateJoinToInvitationRoom.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    roomId = it.data!!
                    myRole = FirebaseProvider.GUEST
                    viewModel.observeQuestionsAddingStatus(it.data)
                }
                is DataState.Error -> {
                    Log.i(TAG, "observeDataStateInvitationAnswer: $it")
                }
                is DataState.Loading -> {

                }
                is DataState.Warning -> {

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