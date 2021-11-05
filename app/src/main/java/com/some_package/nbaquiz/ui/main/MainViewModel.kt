package com.some_package.nbaquiz.ui.main

import androidx.lifecycle.*
import com.some_package.nbaquiz.firebase.FirebaseProvider
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
class MainViewModel @Inject constructor(private val localRepository: LocalRepository , private val firebaseRepository: FirebaseRepository
, private val savedStateHandle: SavedStateHandle ) : ViewModel() {

    private val _userData:MutableLiveData<User> = MutableLiveData()
    val userData:LiveData<User>
        get() = _userData

    private val _dataStateRanks:MutableLiveData<DataState<List<User>>> = MutableLiveData()
    val dataStateRanks:LiveData<DataState<List<User>>>
        get() = _dataStateRanks

    private val _dataStateEditProfile:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateEditProfile:LiveData<DataState<String>>
        get() = _dataStateEditProfile

    private val _dataStateSearchedUser:MutableLiveData<DataState<List<User>>> = MutableLiveData()
    val dataStateSearchedUser:LiveData<DataState<List<User>>>
        get() = _dataStateSearchedUser

    private val _dataStateMyInvitationState:MutableLiveData<DataState<User>> = MutableLiveData()
    val dataStateMyInvitationState:LiveData<DataState<User>>
        get() = _dataStateMyInvitationState

    fun initUserData(){
        _userData.value = localRepository.getUserFromPref()
    }

    fun getRanks(){
        viewModelScope.launch {
            firebaseRepository.getRanks().collect {
                _dataStateRanks.value = it
            }
        }
    }

    fun editProfile(avatar:Int? , team:Int?){
        var mAvatar:Int? = null
        var mTeam:Int? = null
        if (avatar != _userData.value!!.avatar) mAvatar = avatar
        if (team != _userData.value!!.team) mTeam = team

        viewModelScope.launch {
            firebaseRepository.editProfile(mAvatar,mTeam).collect {
                _dataStateEditProfile.value = it
            }
        }

    }

    fun searchUser(username:String){
        viewModelScope.launch {
            firebaseRepository.searchUser(username.toLowerCase()).collect {
                _dataStateSearchedUser.value = it
            }
        }
    }

    fun observeInvitation(){
        viewModelScope.launch {
            firebaseRepository.observeInvitation().collect {
                if (it is DataState.Success){
                    getInviterInfo(it.data!!)
                }
            }
        }
    }

    fun detachObserveInvitation(){
        firebaseRepository.detachObserveInvitation()
    }

    private suspend fun getInviterInfo(userId:String){
        firebaseRepository.getInviterInfo(userId).collect {
            _dataStateMyInvitationState.value = it
        }
    }

    fun answerToInvite(answer:Int){
        viewModelScope.launch {
            if (answer == FirebaseProvider.INVITE_ANSWER_DECLINE) declineInvite()
            firebaseRepository.answerToInvitation(answer).collect {

            }
        }
    }

    private suspend fun declineInvite(){
        firebaseRepository.resetUserAttrsInRealTime(resetStatus = true,resetRivalId = true,resetRoomId = true,resetAnswerStatus = true).collect {

        }
    }

    fun resetUserInRealTime(resetStatus:Boolean,resetRivalId:Boolean,resetRoomId:Boolean,resetAnswerStatus:Boolean){
        viewModelScope.launch {
            firebaseRepository.resetUserAttrsInRealTime(resetStatus = resetStatus , resetRivalId = resetRivalId , resetRoomId = resetRoomId , resetAnswerStatus = resetAnswerStatus).collect {

            }
        }
    }


}