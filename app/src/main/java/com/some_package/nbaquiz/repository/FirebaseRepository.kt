package com.some_package.nbaquiz.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.firebase.FirebaseService
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.preferences.UserSharedPref
import com.some_package.nbaquiz.util.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.StringBuilder
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseRepository @Inject constructor(private val firebaseProvider: FirebaseProvider , private val userSharedPref: UserSharedPref) : FirebaseService {

    private val TAG = "FirebaseRepository"

    override suspend fun registerUser(user: User): Flow<DataState<User>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).add(user).addOnSuccessListener { it ->
            val id: String = it.id
            it.update("id", id)
            //add to realTime
            val hashMap: HashMap<String, Any?> = HashMap()
            hashMap["status"] = firebaseProvider.EMPTY
            hashMap["room-status"] = false
            hashMap["username"] = user.username
            firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_USERS).child(id).updateChildren(hashMap).addOnSuccessListener {
                Log.i(TAG, "registerUser: realtime register")
                user.id = id
                userSharedPref.setUserPref(user = user)
                offer(DataState.Success(user))

            }.addOnFailureListener {
                offer(DataState.Error(it))
                Log.i(TAG, "registerUser: $it")
            }
        }.addOnFailureListener {
            offer(DataState.Error(it))
            Log.i(TAG, "registerUser: $it")
        }
        awaitClose {
            cancel()
        }
    }

    override suspend fun checkUsername(username: String): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).whereEqualTo("lowerCaseUsername",username.toLowerCase()).get().addOnSuccessListener {
            Log.i(TAG, "checkUsername: Umad hoooo yaaahhh")
            if (it.isEmpty){
                offer(DataState.Success(username))
            }else{
                offer(DataState.Error(Exception("This name exists already !")))
            }

        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun getRanks(): Flow<DataState<List<User>>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).orderBy("points",Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener {
            Log.i(TAG, "getRanks: Rank Umad")
            val users:ArrayList<User> = ArrayList()
            if (!it.isEmpty){
                val docs = it.documents
                for (doc in docs){
                    users.add(doc.toObject(User::class.java)!!)
                }
            }
            offer(DataState.Success(users))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }

    }

    override suspend fun editProfile(avatar:Int? , team:Int?): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)

        if (avatar !=null && team !=null){
            firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).document("${userSharedPref.getUserPref().id}")
                .update("avatar",avatar,"team",team).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = avatar ,team = team)
                    offer(DataState.Success("updated"))

                }.addOnFailureListener {
                    offer(DataState.Error(it))
                }
        }

        else if (avatar !=null && team == null){
            firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).document("${userSharedPref.getUserPref().id}")
                .update("avatar",avatar).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = avatar ,team = null)
                    offer(DataState.Success("updated"))

                }.addOnFailureListener {
                    offer(DataState.Error(it))
                }
        }

        else if (avatar ==null && team != null){
            firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).document("${userSharedPref.getUserPref().id}")
                .update("team",team).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = null ,team = team)
                    offer(DataState.Success("updated"))

                }.addOnFailureListener {
                    offer(DataState.Error(it))
                }
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun searchUser(username: String): Flow<DataState<List<User>>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.fireStore.collection(firebaseProvider.USERS_COLLECTION_KEY).whereGreaterThanOrEqualTo("lowerCaseUsername",username).get().addOnSuccessListener {
            val userList:ArrayList<User> = ArrayList()
            for (ds in it.documents){
                userList.add(ds.toObject(User::class.java)!!)
            }
            offer(DataState.Success(userList))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }

    }

    override suspend fun getQuestionsFromFireStore(): Flow<DataState<List<Question>>> = callbackFlow {
        offer(DataState.Loading)
        val questionList:ArrayList<Question> = ArrayList()
        // todo we must get randomly
        firebaseProvider.fireStore.collection(firebaseProvider.QUESTIONS_COLLECTION_KEY).limit(12).get().addOnSuccessListener {

            for (ds in it.documents){
                questionList.add(ds.toObject(Question::class.java)!!)
            }
            offer(DataState.Success(questionList))

        }.addOnFailureListener {
            offer(DataState.Error(it))
        }
    }

    override suspend fun addQuestionsToRooms(collectionId: String, questionsList: List<Question>): Flow<DataState<String>> = callbackFlow {
        // todo can be changed !!!
        offer(DataState.Loading)
        val mRoom:CollectionReference = firebaseProvider.fireStore.collection(firebaseProvider.ROOMS_COLLECTION_KEY).document(firebaseProvider.ROOMS_COLLECTION_KEY).collection(collectionId)
        // add from 0 to n-1
        for(i in 0 until questionsList.size-1){
            mRoom.add(questionsList[i])
        }
        // add n
        mRoom.add(questionsList[questionsList.size-1]).addOnSuccessListener {
            offer(DataState.Success("all questions added"))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun getQuestionsFromRooms(collectionId: String): Flow<DataState<List<Question>>> = callbackFlow {
        offer(DataState.Loading)
        val mRoom:CollectionReference = firebaseProvider.fireStore.collection(firebaseProvider.ROOMS_COLLECTION_KEY).document(firebaseProvider.ROOMS_COLLECTION_KEY).collection(collectionId)
        val questionsList:ArrayList<Question> = ArrayList()
        mRoom.orderBy("id",Query.Direction.ASCENDING).get().addOnSuccessListener {

            for (ds in it.documents){
                questionsList.add(ds.toObject(Question::class.java)!!)
                // TODO: 12/03/2021 be careful!!! : all questions must be deleted even P2 close app before get questions!!!!!! (in this case : delete by P2)
                ds.reference.delete()
            }

            offer(DataState.Success(questionsList))

        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

}