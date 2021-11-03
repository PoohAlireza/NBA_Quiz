package com.some_package.nbaquiz.ui.match

import androidx.lifecycle.*
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.repository.FirebaseRepository
import com.some_package.nbaquiz.repository.LocalRepository
import com.some_package.nbaquiz.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@ExperimentalCoroutinesApi
@HiltViewModel
class WaitingViewModel @Inject constructor(private val localRepository: LocalRepository, private val firebaseRepository: FirebaseRepository, private val savedStateHandle: SavedStateHandle):ViewModel() {

    private val _userData: MutableLiveData<User> = MutableLiveData()
    val userData: LiveData<User>
        get() = _userData

    private val _dataStateJoiningRoom:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateJoiningRoom:LiveData<DataState<String>>
        get() = _dataStateJoiningRoom

    private val _dataStateCreationRoom:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateCreationRoom:LiveData<DataState<String>>
        get() = _dataStateCreationRoom

    private val _dataStateQuestions:MutableLiveData<DataState<List<Question>>> = MutableLiveData()
    val dataStateQuestions:LiveData<DataState<List<Question>>>
        get() = _dataStateQuestions

    private val _dataStateStartingStatus:MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val dataStateStartingStatus:LiveData<DataState<Boolean>>
        get() = _dataStateStartingStatus

    private val _dataStateMyStatus:MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateMyStatus:LiveData<DataState<Int>>
        get() = _dataStateMyStatus

    private val _dataStateObservePlayer2:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateObservePlayer2:LiveData<DataState<String>>
        get() = _dataStateObservePlayer2

    private val _dataStateRivalInfo:MutableLiveData<DataState<Map<String, Any?>>> = MutableLiveData()
    val dataStateRivalInfo:LiveData<DataState<Map<String, Any?>>>
        get() = _dataStateRivalInfo

    //invite
    private val _dataStateSendInvitation:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateSendInvitation:LiveData<DataState<String>>
        get() = _dataStateSendInvitation

    private val _dataStateInvitationAnswer:MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateInvitationAnswer:LiveData<DataState<Int>>
        get() = _dataStateInvitationAnswer

    private val _dataStateInvitationRoom:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateInvitationRoom:LiveData<DataState<String>>
        get() = _dataStateInvitationRoom

    fun initUserData(){
        _userData.value = localRepository.getUserFromPref()
    }
    //P2
    fun joinRoom(){
        viewModelScope.launch {
            firebaseRepository.joinRoom().collect {
                _dataStateJoiningRoom.value = it
            }
        }
    }
    //P2
    fun observeQuestionsAddingStatus(roomId:String){
        viewModelScope.launch {
            firebaseRepository.observeQuestionsAddingStatus(roomId).collect { questionsStatus ->
                if (questionsStatus){
                    getQuestionsFromRoom(roomId)
                }
            }
        }
    }
    //P2
    private fun getQuestionsFromRoom(roomId: String){
        viewModelScope.launch {
            firebaseRepository.getQuestionsFromRooms(roomId).collect {
                //getQuestions
                _dataStateQuestions.value=it
            }
        }
    }
    //P2
    fun changeStartingGameStatus(roomId: String){
        viewModelScope.launch {
            firebaseRepository.changeStartingGameStatus(roomId).collect {
                _dataStateStartingStatus.value=it
            }
        }
    }
    //P1 & P2
    fun setImBusy(){
        viewModelScope.launch {
            firebaseRepository.setImBusy().collect {
                _dataStateMyStatus.value = it
            }
        }
    }
    //P1
    fun createRoom(){
        viewModelScope.launch {
            firebaseRepository.createRoom().collect {
                _dataStateCreationRoom.value = it
            }
        }
    }
    //P1
    fun observeP2(roomId: String){
        viewModelScope.launch {
            firebaseRepository.observeP2(roomId).collect {
                if (it is DataState.Success){
                    _dataStateObservePlayer2.value = it
                }
            }
        }
    }
    //P1
    fun getRandomQuestionsFromFireStore(roomId: String){
        viewModelScope.launch {
            firebaseRepository.getQuestionsFromFireStore().collect {
                if (it is DataState.Success){
                    _dataStateQuestions.value = it
                    setQuestionsForRoom(roomId,it.data!!)
                }
            }
        }
    }
    //P1
    private fun setQuestionsForRoom(roomId: String , questionList: List<Question>){
        viewModelScope.launch {
            firebaseRepository.addQuestionsToRooms(roomId,questionList).collect {
                if (it is DataState.Success){
                    setQuestionsPreparingStatus(roomId)
                }
            }
        }
    }
    //P1
    private fun setQuestionsPreparingStatus(roomId: String){
        viewModelScope.launch {
            firebaseRepository.setQuestionsPreparingStatus(roomId).collect {
                if (it is DataState.Success){
                    observeStartingGameStatus(roomId)
                }
            }
        }
    }
    //P1
    private fun observeStartingGameStatus(roomId: String){
        viewModelScope.launch {
            firebaseRepository.observeStartingGameStatus(roomId).collect {
                if(it is DataState.Success){
                    _dataStateStartingStatus.value = it
                }
            }
        }
    }
    //P1 & P2
    fun getRivalInfo(roomId: String,role:String){
        viewModelScope.launch {
            firebaseRepository.getPlayerInfo(roomId,role).collect {
                _dataStateRivalInfo.value = it
            }
        }
    }


    //invite
    //P1
    fun sendInvite(rivalId:String){
        viewModelScope.launch {
            firebaseRepository.sendInvitation(rivalId).collect {
                _dataStateSendInvitation.value = it
            }
        }
    }
    //P1
    fun observeInvitationAnswer(rivalId:String){
        viewModelScope.launch {
            firebaseRepository.observeInvitationAnswer(rivalId).collect {
                _dataStateInvitationAnswer.value = it
            }
        }
    }
    //P1
    fun createInvitationRoom(){
        viewModelScope.launch {
            firebaseRepository.createInvitationRoom().collect {
                _dataStateInvitationRoom.value = it
            }
        }
    }
    //P1
    fun setRoomIdForInvitedPlayer(userId:String , roomId: String){
        viewModelScope.launch {
            firebaseRepository.setRoomId(roomId = roomId , userId =  userId).collect {

            }
        }
    }


}