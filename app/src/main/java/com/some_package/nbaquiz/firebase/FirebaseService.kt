package com.some_package.nbaquiz.firebase

import android.content.Context
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.DataState
import kotlinx.coroutines.flow.Flow

interface FirebaseService {

    suspend fun registerUser(user: User):Flow<DataState<User>>
    suspend fun checkUsername(username:String):Flow<DataState<String>>
    suspend fun getRanks():Flow<DataState<List<User>>>
    suspend fun editProfile(avatar:Int? , team:Int?):Flow<DataState<String>>
    suspend fun searchUser(username:String):Flow<DataState<List<User>>>
    suspend fun getQuestionsFromFireStore():Flow<DataState<List<Question>>>
    suspend fun addQuestionsToRooms(collectionId:String , questionsList:List<Question>):Flow<DataState<String>>
    suspend fun getQuestionsFromRooms(collectionId: String):Flow<DataState<List<Question>>>
    suspend fun joinRoom():Flow<DataState<String>>
    suspend fun createRoom():Flow<DataState<String>>
    suspend fun observeQuestionsAddingStatus(roomId:String):Flow<Boolean>
    suspend fun changeStartingGameStatus(roomId: String):Flow<DataState<Boolean>>
    suspend fun observeStartingGameStatus(roomId: String):Flow<DataState<Boolean>>
    suspend fun setImBusy():Flow<DataState<Int>>
    suspend fun observeP2(roomId:String):Flow<DataState<String>>
    suspend fun setQuestionsPreparingStatus(roomId: String):Flow<DataState<Boolean>>
    /**
     * here my role argument is player identifier . for example : P1 OR P2
     * this identifier attach to a string like : P1-username or P2-avatar
     * */
    suspend fun getPlayerInfo(roomId: String,playerRole:String):Flow<DataState<Map<String,Any?>>>
}