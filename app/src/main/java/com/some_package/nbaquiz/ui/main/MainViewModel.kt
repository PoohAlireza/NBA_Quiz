package com.some_package.nbaquiz.ui.main

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
class MainViewModel @Inject constructor(private val localRepository: LocalRepository , private val firebaseRepository: FirebaseRepository, private val savedStateHandle: SavedStateHandle ) : ViewModel() {


    private val _userData:MutableLiveData<User> = MutableLiveData()
    val userData:LiveData<User>
        get() = _userData

    private val _dataStateRanks:MutableLiveData<DataState<List<User>>> = MutableLiveData()
    val dataStateRanks:LiveData<DataState<List<User>>>
        get() = _dataStateRanks

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


}