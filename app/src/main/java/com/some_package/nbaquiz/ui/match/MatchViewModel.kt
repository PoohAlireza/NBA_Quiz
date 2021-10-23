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

    private val _dataStateAnswer: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateAnswer: LiveData<DataState<Int>>
        get() = _dataStateAnswer

    private val _dataStateQuarter: MutableLiveData<DataState<Int>> = MutableLiveData()
    val dataStateQuarter: LiveData<DataState<Int>>
        get() = _dataStateQuarter

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

    fun observeAnswer(roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.observeAnswerState(roomId, playerRole).collect {
                _dataStateAnswer.value = it
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

    fun setAnswer(answer:Int,roomId:String,playerRole:String){
        viewModelScope.launch {
            firebaseRepository.setAnswerState(roomId,answer, playerRole).collect {
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
                Log.i(TAG, "addTime: $it")
            }
        }
    }




}