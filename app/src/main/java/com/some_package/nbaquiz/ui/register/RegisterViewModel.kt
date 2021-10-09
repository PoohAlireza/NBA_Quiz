package com.some_package.nbaquiz.ui.register

import androidx.lifecycle.*
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.repository.FirebaseRepository
import com.some_package.nbaquiz.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class RegisterViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {


    private val _dataStateRegister:MutableLiveData<DataState<User>> = MutableLiveData()
    val dataStateRegister:LiveData<DataState<User>>
        get() = _dataStateRegister

    private val _dataStateCheckUsername:MutableLiveData<DataState<String>> = MutableLiveData()
    val dataStateCheckUsername:LiveData<DataState<String>>
        get() = _dataStateCheckUsername

    private val _avatar:MutableLiveData<Int> = MutableLiveData()
    val avatar:LiveData<Int>
        get() = _avatar

    private val _team:MutableLiveData<Int> = MutableLiveData()
    val team:LiveData<Int>
        get() = _team

    fun setAvatar(value:Int){
        _avatar.value = value
    }

    fun setTeam(value:Int){
        _team.value = value
    }


    fun registerUser(user:User){
        viewModelScope.launch {
            firebaseRepository.registerUser(user).collect {
                _dataStateRegister.value = it
            }
        }
    }


    fun checkUsername(username:String){
        if (username.length < 3){
            _dataStateCheckUsername.value = DataState.Error(Exception("Name must be at least 3 characters long !"))
            return
        }
        viewModelScope.launch {
            firebaseRepository.checkUsername(username).collect {
                _dataStateCheckUsername.value=it
            }
        }
    }


    fun backFromChooseAvatarToChooseName(){
        _dataStateCheckUsername.value = null
    }


}