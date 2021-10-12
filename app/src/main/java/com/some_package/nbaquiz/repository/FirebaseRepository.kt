package com.some_package.nbaquiz.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.firebase.FirebaseService
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.preferences.UserSharedPref
import com.some_package.nbaquiz.util.DataState
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.lang.StringBuilder
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

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


    // 1. first try to join a room then observe ::: if be able to join go line 3 otherwise go line 2
    // 2. we observe an exception so we try to create a room by calling createRoom method and go line 3
    // 3. so we have a roomId and are in a room ::: if we are P1 go line 4 otherwise go line 5
    // 4. we are P1 so must observe P2 and then if P2 joined get questions and create a room
    // 5. we are P2 so must observe questions and room creation status
    // ...
    override suspend fun joinRoom(): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val roomsRef = firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS)
        roomsRef.get().addOnSuccessListener {
            try {
                var roomId:String? = null
                if (it.exists()){
                    for (ds in it.children){
                        if (ds.exists() && ds.child("can-join").getValue(Boolean::class.java)!!){
                            ds.child("can-join").ref.setValue(false)
                            ds.child("P2-username").ref.setValue(userSharedPref.getUserPref().username)
                            roomId = ds.key
                            break
                        }
                    }
                    if (roomId != null){
                        //how to delay and then check username
                            //todo delay 1 sec here
                        if (roomsRef.child(roomId).child("P2-username").equals(userSharedPref.getUserPref().username)){
                            offer(DataState.Success(roomId))
                        }else{
                            offer(DataState.Error(Exception("not_founded")))
                        }
                    }else{
                        offer(DataState.Error(Exception("not_founded")))
                    }
                }
            }catch (e:Exception){
                offer(DataState.Error(e))
            }

        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun createRoom(): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val roomId = firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS).push().key
        val roomRef = firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS).child(roomId!!).ref
        val children:HashMap<String,Any?> = HashMap()
        children["can-join"] = true
        children["start"] = false
        children["questions-ready"] = false
        children["P1-username"] = userSharedPref.getUserPref().username
        children["P2-username"] = ""
        children["P1-avatar"] = userSharedPref.getUserPref().avatar
        children["P2-avatar"] = 0
        children["P1-team"] = userSharedPref.getUserPref().team
        children["P2-team"] = 0
        children["P1-point"] = 0
        children["P2-point"] = 0
        children["P1-time"] = 0
        children["P2-time"] = 0
        children["P1-quarter"] = 1
        children["P2-quarter"] = 1
        children["P1-answer"] = firebaseProvider.ANSWER_EMPTY
        children["P2-answer"] = firebaseProvider.ANSWER_EMPTY
        roomRef.updateChildren(children).addOnSuccessListener {
            offer(DataState.Success(roomId))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }


    }

    override suspend fun observeQuestionsAddingStatus(roomId:String): Flow<Boolean> = callbackFlow {
        firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS).child(roomId).child("questions-ready").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(Boolean::class.java)!!){
                    //its true
                    offer(snapshot.getValue(Boolean::class.java)!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        awaitClose {
            cancel()
        }
    }

    override suspend fun changeStartingGameStatus(roomId: String): Flow<DataState<Boolean>> = callbackFlow {

        firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS).child(roomId).child("start").setValue(true).addOnSuccessListener {
            offer(DataState.Success(true))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun observeStartingGameStatus(roomId: String): Flow<DataState<Boolean>> = callbackFlow {

        firebaseProvider.realTime.getReference(firebaseProvider.REALTIME_ROOMS).child(roomId).child("start").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(Boolean::class.java)!!){
                    //start game
                    offer(DataState.Success(true))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        awaitClose{
            cancel()
        }
    }
}