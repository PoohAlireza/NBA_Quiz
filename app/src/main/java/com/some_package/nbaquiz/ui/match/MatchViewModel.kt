package com.some_package.nbaquiz.ui.match

import android.util.Log
import androidx.lifecycle.*
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
class MatchViewModel @Inject constructor(private val localRepository: LocalRepository , private val firebaseRepository: FirebaseRepository
, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val TAG = "MatchViewModel"

    private val _userData: MutableLiveData<User> = MutableLiveData()
    val userData: LiveData<User>
        get() = _userData

    private val _dataStatePoint: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStatePoint: LiveData<DataState<Int>>
        get() = _dataStatePoint

    private val _dataStateAnswer1: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateAnswer1: LiveData<DataState<Int>>
        get() = _dataStateAnswer1

    private val _dataStateAnswer2: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateAnswer2: LiveData<DataState<Int>>
        get() = _dataStateAnswer2

    private val _dataStateAnswer3: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateAnswer3: LiveData<DataState<Int>>
        get() = _dataStateAnswer3

    private val _dataStateQuarter: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateQuarter: LiveData<DataState<Int>>
        get() = _dataStateQuarter

    private val _dataStateGetTime: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateGetTime: LiveData<DataState<Int>>
        get() = _dataStateGetTime

    private val _dataStateSetTime: MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateSetTime: LiveData<DataState<String>>
        get() = _dataStateSetTime


    fun initUserData(){
        _userData.value = localRepository.getUserFromPref()
    }

    fun observePoint(roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.observePoint(roomId,playerRole).collect {
                _dataStatePoint.value = it
            }
        }
    }

    fun observeAnswer1(roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.observeAnswerState1(roomId, playerRole).collect {
                _dataStateAnswer1.value = it
            }
        }
    }

    fun observeAnswer2(roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.observeAnswerState2(roomId, playerRole).collect {
                _dataStateAnswer2.value = it
            }
        }
    }

    fun observeAnswer3(roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.observeAnswerState3(roomId, playerRole).collect {
                _dataStateAnswer3.value = it
            }
        }
    }

    fun observeQuarter(roomId:String){
        viewModelScope.launch {
            firebaseRepository.observeQuarterNumber(roomId).collect {
                _dataStateQuarter.value = it
            }
        }
    }

    fun setPoint(point:Int,roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.addPoint(roomId,point,playerRole).collect {
                Log.i(TAG, "setPoint: $it")
            }
        }
    }

    fun setAnswer(answer:Int,roomId:String,playerRole:String,state:Int){
        viewModelScope.launch {
            firebaseRepository.setAnswerState(roomId,answer, playerRole,state).collect {
                Log.i(TAG, "setAnswer: $it")
            }
        }
    }

    fun setQuarter(roomId: String){
        viewModelScope.launch {
            firebaseRepository.setQuarterNumber(roomId).collect {
                Log.i(TAG, "setQuarter: $it")
            }
        }
    }

    fun addTime(roomId: String,time:Int,playerRole: String){
        viewModelScope.launch {
            firebaseRepository.addTime(roomId, time, playerRole).collect {
                if (it is DataState.Success){
                    _dataStateSetTime.value = it
                }
            }
        }
    }

    fun getTime(roomId: String,playerRole: String){
        viewModelScope.launch {
            firebaseRepository.getTime(roomId,playerRole).collect {
                if(it is DataState.Success){
                    _dataStateGetTime.value=it
                }
            }
        }
    }

    fun incrementGame(){
        viewModelScope.launch {
            firebaseRepository.incrementGame().collect {
                if (it is DataState.Error){
                    Log.i(TAG, "incrementGame: $it")
                }
            }
        }
    }

    fun deleteRoom(roomId:String){
        viewModelScope.launch {
            firebaseRepository.deleteRoomAfterMatch(roomId).collect {
                if (it is DataState.Error){
                    Log.i(TAG, "deleteRoom: $it")
                }
            }
        }
    }

    fun detachMatchListener(){
        firebaseRepository.detachGameListeners()
    }

}