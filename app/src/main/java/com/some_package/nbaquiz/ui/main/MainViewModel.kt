package com.some_package.nbaquiz.ui.main

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
import java.lang.StringBuilder
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(private val localRepository: LocalRepository , private val firebaseRepository: FirebaseRepository, private val savedStateHandle: SavedStateHandle ) : ViewModel() {


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

    fun joinRoom(){
        viewModelScope.launch {
            firebaseRepository.joinRoom().collect {
                _dataStateJoiningRoom.value = it
            }
        }
    }

    fun createRoom(){
        viewModelScope.launch {
            firebaseRepository.createRoom().collect {
                _dataStateCreationRoom.value = it
            }
        }
    }

    fun observeQuestionsAddingStatus(roomId:String){
        viewModelScope.launch {
            firebaseRepository.observeQuestionsAddingStatus(roomId).collect { questionsStatus ->
                if (questionsStatus){
                    firebaseRepository.getQuestionsFromRooms(roomId).collect {
                        //getQuestions
                        _dataStateQuestions.value=it
                    }
                }
            }
        }
    }

    fun changeStartingGameStatus(roomId: String){
        viewModelScope.launch {
            firebaseRepository.changeStartingGameStatus(roomId).collect {
                _dataStateStartingStatus.value=it
            }
        }
    }


}