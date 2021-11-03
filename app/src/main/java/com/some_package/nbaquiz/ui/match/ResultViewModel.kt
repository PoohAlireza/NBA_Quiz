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
class ResultViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository , private val localRepository: LocalRepository , private val savedStateHandle: SavedStateHandle):ViewModel() {
    private val TAG = "ResultViewModel"
    private val _userData: MutableLiveData<User> = MutableLiveData()
    val userData: LiveData<User>
        get() = _userData

    fun initUserData(){
        _userData.value = localRepository.getUserFromPref()
    }

    fun incrementWin(){
        viewModelScope.launch {
            firebaseRepository.incrementWin().collect {
                if (it is DataState.Error){
                    Log.i(TAG, "incrementWin: $it")
                }
            }
        }
    }

    fun incrementPoint(point:Int){
        viewModelScope.launch {
            firebaseRepository.addPointsToUser(point).collect {
                if (it is DataState.Error){
                    Log.i(TAG, "incrementPoint: $it")
                }
            }
        }
    }

}