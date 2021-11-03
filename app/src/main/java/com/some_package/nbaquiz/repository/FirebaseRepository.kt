package com.some_package.nbaquiz.repository

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.some_package.nbaquiz.firebase.FirebaseProvider
import com.some_package.nbaquiz.firebase.FirebaseService
import com.some_package.nbaquiz.model.Question
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.preferences.UserSharedPref
import com.some_package.nbaquiz.util.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseRepository @Inject constructor(
    private val firebaseProvider: FirebaseProvider,
    private val userSharedPref: UserSharedPref
) : FirebaseService {

    private val TAG = "FirebaseRepository"

    private val gameListeners = HashMap<DatabaseReference, ValueEventListener>()
    private val roomPreparingListeners = HashMap<DatabaseReference, ValueEventListener>()

    override suspend fun registerUser(user: User): Flow<DataState<User>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).add(user).addOnSuccessListener { it ->
            val id: String = it.id
            it.update("id", id)
            //add to realTime
            val hashMap: HashMap<String, Any?> = HashMap()
            hashMap["status"] = FirebaseProvider.EMPTY
            hashMap["accept"] = FirebaseProvider.INVITE_ANSWER_EMPTY
            hashMap["rival-id"] = ""
            hashMap["room-id"] = ""
            firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(id).updateChildren(
                hashMap
            ).addOnSuccessListener {
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
        firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).whereEqualTo(
            "lowerCaseUsername",
            username.toLowerCase()
        ).get().addOnSuccessListener {
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
        firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).orderBy(
            "points",
            Query.Direction.DESCENDING
        ).limit(10).get().addOnSuccessListener {
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

    override suspend fun editProfile(avatar: Int?, team: Int?): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)

        if (avatar !=null && team !=null){
            firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document(
                userSharedPref.getUserPref().id!!
            )
                .update("avatar", avatar, "team", team).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = avatar, team = team)
                    offer(DataState.Success("updated"))

                }.addOnFailureListener {
                    offer(DataState.Error(it))
                }
        }

        else if (avatar !=null && team == null){
            firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document("${userSharedPref.getUserPref().id}")
                .update("avatar", avatar).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = avatar, team = null)
                    offer(DataState.Success("updated"))

                }.addOnFailureListener {
                    offer(DataState.Error(it))
                }
        }

        else if (avatar ==null && team != null){
            firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document("${userSharedPref.getUserPref().id}")
                .update("team", team).addOnSuccessListener {

                    userSharedPref.updateUserPref(avatar = null, team = team)
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
        firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).whereGreaterThanOrEqualTo(
            "lowerCaseUsername",
            username
        ).get().addOnSuccessListener {
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
        firebaseProvider.fireStore.collection(FirebaseProvider.QUESTIONS_COLLECTION_KEY).limit(12).orderBy(
            "id",
            Query.Direction.ASCENDING
        ).get().addOnSuccessListener {

            for (ds in it.documents){
                questionList.add(ds.toObject(Question::class.java)!!)
            }
            Log.i(TAG, "getQuestionsFromFireStore: umad")
            offer(DataState.Success(questionList))
            Log.i(TAG, "getQuestionsFromRooms: $questionList")

        }.addOnFailureListener {
            Log.i(TAG, "getQuestionsFromFireStore: error")
            offer(DataState.Error(it))
        }
        awaitClose {
            cancel()
        }
    }

    override suspend fun addQuestionsToRooms(collectionId: String, questionsList: List<Question>): Flow<DataState<String>> = callbackFlow {
        // todo use batch of write !!!
        offer(DataState.Loading)
        val mRoom = firebaseProvider.fireStore.collection(FirebaseProvider.ROOMS_COLLECTION_KEY).document(
            FirebaseProvider.ROOMS_COLLECTION_KEY
        ).collection(collectionId)
        // add from 0 to n-1
        for(i in 0 until questionsList.size-1){
            mRoom.add(questionsList[i])
        }
        // add n
        mRoom.add(questionsList[questionsList.size - 1]).addOnSuccessListener {
            offer(DataState.Success("all questions added"))
            Log.i(TAG, "addQuestionsToRooms: added")
        }.addOnFailureListener {
            Log.i(TAG, "addQuestionsToRooms: error")
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun getQuestionsFromRooms(collectionId: String): Flow<DataState<List<Question>>> = callbackFlow {
        offer(DataState.Loading)
        val mRoom:CollectionReference = firebaseProvider.fireStore.collection(FirebaseProvider.ROOMS_COLLECTION_KEY).document(
            FirebaseProvider.ROOMS_COLLECTION_KEY
        ).collection(collectionId)
        val questionsList:ArrayList<Question> = ArrayList()
        mRoom.orderBy("id", Query.Direction.ASCENDING).get().addOnSuccessListener {

            for (ds in it.documents){
                questionsList.add(ds.toObject(Question::class.java)!!)
                // TODO: 12/03/2021 be careful!!! : all questions must be deleted even P2 close app before get questions!!!!!! (in this case : delete by P2)
                ds.reference.delete()
            }

            offer(DataState.Success(questionsList))
            Log.i(TAG, "getQuestionsFromRooms: got")
            Log.i(TAG, "getQuestionsFromRooms: $questionsList")

        }.addOnFailureListener {
            Log.i(TAG, "getQuestionsFromRooms: error")
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
    //todo : i think must save the listeners and delete them after their job finished !!!
    //P2
    override suspend fun joinRoom(): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val roomsRef = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS)
        roomsRef.get().addOnSuccessListener {
            try {
                var roomId:String? = null
                if (it.exists()){
                    for (ds in it.children){
                        if (ds.exists() && ds.child("P2-username").getValue(String::class.java)!!.isEmpty()){
                            roomId = ds.key
                            break
                        }
                    }

                    if (roomId != null){
                        roomsRef.child(roomId).child("P2-username").ref.runTransaction(object :
                            Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {

                                currentData.value = userSharedPref.getUserPref().username

                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                Log.i(TAG, "onComplete: $error")
                                if (committed && currentData != null && currentData.getValue(String::class.java)!!
                                        .equals(
                                            userSharedPref.getUserPref().username
                                        )
                                ) {
                                    roomsRef.child(roomId).child("P2-avatar").ref.setValue(
                                        userSharedPref.getUserPref().avatar
                                    )
                                    roomsRef.child(roomId).child("P2-team").ref.setValue(
                                        userSharedPref.getUserPref().team
                                    ).addOnSuccessListener {
                                        offer(DataState.Success(roomId))
                                    }
                                } else {
                                    offer(DataState.Error(Exception("not_founded")))
                                }
                            }
                        })
                    }else{
                        offer(DataState.Error(Exception("not_founded")))
                        Log.i(TAG, "joinRoom: error not found")
                    }
                }else{
                    offer(DataState.Error(Exception("not_founded")))
                    Log.i(TAG, "joinRoom: error not found")
                }
            }catch (e: Exception){
                offer(DataState.Error(e))
                Log.i(TAG, "joinRoom: error exception")
            }

        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P1
    override suspend fun createRoom(): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val roomId = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).push().key
        val roomRef = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId!!
        ).ref
        val children:HashMap<String, Any?> = HashMap()
        children["start"] = false
        children["questions-ready"] = false
        children["P1-username"] = userSharedPref.getUserPref().username
        children["P2-username"] = ""
        children["P1-avatar"] = userSharedPref.getUserPref().avatar
        children["P2-avatar"] = -1
        children["P1-team"] = userSharedPref.getUserPref().team
        children["P2-team"] = -1
        children["P1-point"] = 0
        children["P2-point"] = 0
        children["P1-time"] = 0
        children["P2-time"] = 0
        children["quarter"] = 0
        children["P1-answer1"] = FirebaseProvider.ANSWER_EMPTY
        children["P1-answer2"] = FirebaseProvider.ANSWER_EMPTY
        children["P1-answer3"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer1"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer2"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer3"] = FirebaseProvider.ANSWER_EMPTY
        roomRef.updateChildren(children).addOnSuccessListener {
            offer(DataState.Success(roomId))
            Log.i(TAG, "createRoom: success")
        }.addOnFailureListener {
            Log.i(TAG, "createRoom: error")
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }

    }
    //P2
    //*
    override suspend fun observeQuestionsAddingStatus(roomId: String): Flow<Boolean> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId
        ).child("questions-ready")
        val listener = ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.getValue(Boolean::class.java)!!) {
                        //its true
                        offer(snapshot.getValue(Boolean::class.java)!!)
                        Log.i(TAG, "observeQuestionsAddingStatus: success")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "observeQuestionsAddingStatus: error")
                    TODO("Not yet implemented")
                }

            })
        roomPreparingListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }
    //P2
    override suspend fun changeStartingGameStatus(roomId: String): Flow<DataState<Boolean>> = callbackFlow {

        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child(
            "start"
        ).setValue(true).addOnSuccessListener {
            offer(DataState.Success(true))
            Log.i(TAG, "changeStartingGameStatus: success")
        }.addOnFailureListener {
            Log.i(TAG, "changeStartingGameStatus: error")
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P1
    //*
    override suspend fun observeStartingGameStatus(roomId: String): Flow<DataState<Boolean>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId
        ).child("start")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(Boolean::class.java)!!) {
                    //start game
                    offer(DataState.Success(true))
                    Log.i(TAG, "observeStartingGameStatus: success")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "observeStartingGameStatus: error")
                TODO("Not yet implemented")
            }

        })
        roomPreparingListeners[ref] = listener
        awaitClose{
            cancel()
        }
    }
    //P1 & P2
    override suspend fun setImBusy(): Flow<DataState<Int>> = callbackFlow {
        offer(DataState.Loading)

        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!).child(
            "status"
        ).setValue(FirebaseProvider.BUSY).addOnSuccessListener {
            offer(DataState.Success(FirebaseProvider.BUSY))
            Log.i(TAG, "setImBusy: set")
        }.addOnFailureListener {
            Log.i(TAG, "setImBusy: error")
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P1
    //*
    override suspend fun observeP2(roomId: String): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId
        ).child("P2-team")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(Int::class.java) != -1) {
                    // or get username and offer
                    offer(DataState.Success("Founded"))
                    Log.i(TAG, "observeP2: success")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "observeP2: error")
                TODO("Not yet implemented")
            }

        })
        roomPreparingListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }
    //P1
    override suspend fun setQuestionsPreparingStatus(roomId: String): Flow<DataState<Boolean>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child(
            "questions-ready"
        ).setValue(true).addOnSuccessListener {
            offer(DataState.Success(true))
            Log.i(TAG, "setQuestionsPreparingStatus: set")
        }.addOnFailureListener {
            Log.i(TAG, "setQuestionsPreparingStatus: error")
            offer(DataState.Error(it))
        }


        awaitClose { cancel() }
    }
    //P1 & P2
    override suspend fun getPlayerInfo(roomId: String, playerRole: String): Flow<DataState<Map<String, Any?>>> = callbackFlow {
        offer(DataState.Loading)
        val username = "$playerRole-username"
        val avatar = "$playerRole-avatar"
        val team = "$playerRole-team"
        val roomRef = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId
        )
        val map = HashMap<String, Any?>()
        roomRef.child(username).get().addOnSuccessListener { mUsername ->
            map["username"] = mUsername.getValue(String::class.java)
            Log.i(TAG, "getPlayerInfo: username set")

            roomRef.child(avatar).get().addOnSuccessListener { avatar ->
                map["avatar"] = avatar.getValue(Int::class.java)
                Log.i(TAG, "getPlayerInfo: avatar set")


                roomRef.child(team).get().addOnSuccessListener { team ->
                    map["team"] = team.getValue(Int::class.java)
                    Log.i(TAG, "getPlayerInfo: team set")
                    offer(DataState.Success(map))
                }.addOnFailureListener {
                    Log.i(TAG, "getPlayerInfo: team error")
                    offer(DataState.Error(it))
                }


            }.addOnFailureListener {
                Log.i(TAG, "getPlayerInfo: avatar error")
                offer(DataState.Error(it))
            }


        }.addOnFailureListener {
            Log.i(TAG, "getPlayerInfo: username error")
            offer(DataState.Error(it))
        }


        awaitClose {
            cancel()
        }
    }

    //match
    override suspend fun addPoint(roomId: String, point: Int, playerRole: String): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val goal = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-point")
        goal.runTransaction(object : Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentPoint = currentData.getValue(Int::class.java)?: return Transaction.success(currentData)
                currentData.value = currentPoint+point
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed){
                    offer(DataState.Success("point added"))
                }else{
                    offer(DataState.Error(error!!.toException()))
                }
            }

        })

        awaitClose {
            cancel()
        }
    }
    //*
    override suspend fun observePoint(roomId: String, playerRole: String): Flow<DataState<Int>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-point")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offer(DataState.Success(snapshot.getValue(Int::class.java)))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(DataState.Error(error.toException()))
            }
        })
        gameListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }

    override suspend fun setAnswerState(roomId: String, answer: Int, playerRole: String , state:Int): Flow<DataState<String>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-answer$state")
            ref.setValue(FirebaseProvider.ANSWER_EMPTY).addOnSuccessListener {
            ref.setValue(answer).addOnSuccessListener {
                offer(DataState.Success("answer is written"))
            }.addOnFailureListener {
                offer(DataState.Error(it))
            }
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }
        awaitClose {
            cancel()
        }
    }
    //*
    override suspend fun observeAnswerState1(roomId: String, playerRole: String): Flow<DataState<Int>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-answer1")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offer(DataState.Success(snapshot.getValue(Int::class.java)))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(DataState.Error(error.toException()))
            }
        })
        gameListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }
    //*
    override suspend fun observeAnswerState2(roomId: String, playerRole: String): Flow<DataState<Int>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-answer2")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offer(DataState.Success(snapshot.getValue(Int::class.java)))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(DataState.Error(error.toException()))
            }
        })
        gameListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }
    //*
    override suspend fun observeAnswerState3(roomId: String, playerRole: String): Flow<DataState<Int>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("$playerRole-answer3")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offer(DataState.Success(snapshot.getValue(Int::class.java)))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(DataState.Error(error.toException()))
            }
        })
        gameListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }

    override suspend fun setQuarterNumber(roomId: String): Flow<DataState<String>> = callbackFlow {
        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child(
            "quarter"
        ).runTransaction(
            object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var quarter =
                        currentData.getValue(Int::class.java) ?: return Transaction.abort()
                    currentData.value = ++quarter
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed) {
                        offer(DataState.Success("quarter committed"))
                    } else {
                        Log.i(TAG, "onComplete: $error")
                        offer(DataState.Error(error!!.toException()))
                    }
                }
            })
        awaitClose {
            cancel()
        }
    }
    //*
    override suspend fun observeQuarterNumber(roomId: String): Flow<DataState<Int>> = callbackFlow {
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child("quarter")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offer(DataState.Success(snapshot.getValue(Int::class.java)))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(DataState.Error(error.toException()))
            }
        })
        gameListeners[ref] = listener
        awaitClose {
            cancel()
        }
    }

    override suspend fun addTime(roomId: String, time: Int, playerRole: String): Flow<DataState<String>> = callbackFlow {
        val goal = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(
            roomId
        ).child("$playerRole-time")
        goal.get().addOnSuccessListener {
            goal.setValue(it.getValue(Int::class.java)!! + time).addOnSuccessListener {
                offer(DataState.Success("time added"))
            }.addOnFailureListener { errorAdd ->
                offer(DataState.Error(errorAdd))
            }
        }.addOnFailureListener { errorGet ->
            offer(DataState.Error(errorGet))
        }
        awaitClose {
            cancel()
        }
    }

    override suspend fun getTime(roomId: String, playerRole: String): Flow<DataState<Int>> = callbackFlow {
        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).child(
            "$playerRole-time"
        ).get().addOnSuccessListener {
            offer(DataState.Success(it.getValue(Int::class.java)))
        }.addOnFailureListener {
            Log.i(TAG, "getPoint: $it")
            offer(DataState.Error(it))
        }
        awaitClose {
            cancel()
        }
    }

    override suspend fun incrementGame(): Flow<DataState<Int>> = callbackFlow {
        offer(DataState.Loading)
        val mDoc = firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document(
            userSharedPref.getUserPref().id!!
        )
        mDoc.update("games", FieldValue.increment(1)).addOnSuccessListener {
            val newGamesCount = userSharedPref.incrementGame()
            offer(DataState.Success(newGamesCount))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }

    override suspend fun addPointsToUser(points: Int): Flow<DataState<Int>> = callbackFlow {
        offer(DataState.Loading)
        val mDoc = firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document(
            userSharedPref.getUserPref().id!!
        )
        mDoc.update("points", FieldValue.increment(points.toLong())).addOnSuccessListener {
            val newPointsCount = userSharedPref.incrementPoint(points)
            offer(DataState.Success(newPointsCount))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }


        awaitClose {
            cancel()
        }
    }

    override suspend fun incrementWin(): Flow<DataState<Int>> = callbackFlow {
        offer(DataState.Loading)
        val mDoc = firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document(
            userSharedPref.getUserPref().id!!
        )
        mDoc.update("wins", FieldValue.increment(1)).addOnSuccessListener {
            val newWinsCount = userSharedPref.incrementWin()
            offer(DataState.Success(newWinsCount))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }


        awaitClose {
            cancel()
        }
    }

    override suspend fun deleteRoomAfterMatch(roomId: String): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId).removeValue().addOnSuccessListener {
            offer(DataState.Success("removed successfully"))

        }.addOnFailureListener { error ->
            offer(DataState.Error(error))
        }

        awaitClose {
            cancel()
        }
    }

    // TODO: 28/10/2021 call where?
    override suspend fun setImEmpty(): Flow<DataState<Int>> = callbackFlow {

        firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!).child(
            "status"
        )
            .setValue(FirebaseProvider.EMPTY).addOnSuccessListener {
                offer(DataState.Success(FirebaseProvider.EMPTY))
            }.addOnFailureListener {
                offer(DataState.Error(it))
            }

        awaitClose {
            cancel()
        }
    }

    override fun detachRoomPreparingListeners() {
        for (ref in roomPreparingListeners.keys){
            ref.removeEventListener(roomPreparingListeners[ref]!!)
        }
    }

    override fun detachGameListeners() {
        for (ref in gameListeners.keys){
            ref.removeEventListener(gameListeners[ref]!!)
        }
    }

    //invite

    //P1
    override suspend fun sendInvitation(rivalId: String): Flow<DataState<String>> = callbackFlow {
        offer(DataState.Loading)
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(rivalId)
        ref.child("status").get().addOnSuccessListener {
            if (it.exists()){
                val status = it.getValue(Int::class.java)
                if (status == FirebaseProvider.EMPTY){
                    ref.child("status").setValue(FirebaseProvider.BUSY).addOnSuccessListener {
                        ref.child("rival-id").setValue(userSharedPref.getUserPref().id).addOnSuccessListener {
                            offer(DataState.Success("success"))
                        }.addOnFailureListener { errorSetId ->
                            offer(DataState.Error(errorSetId))
                        }
                    }.addOnFailureListener { errorSetStatus ->
                        offer(DataState.Error(errorSetStatus))
                    }
                }else {
                    offer(DataState.Warning("The person you invited is currently in another match"))
                }
            }
        }.addOnFailureListener{
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P2 //**
    override suspend fun observeInvitation(): Flow<DataState<String>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!).child("rival-id")
        val listener = ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(String::class.java)!!.isNotEmpty()){
                    offer(DataState.Success(snapshot.getValue(String::class.java)))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        awaitClose {
            cancel()
        }
    }
    //P2
    override suspend fun getInviterInfo(userId:String): Flow<DataState<User>> = callbackFlow {
        offer(DataState.Loading)
        val ref = firebaseProvider.fireStore.collection(FirebaseProvider.USERS_COLLECTION_KEY).document(userId)
        ref.get().addOnSuccessListener {
            if (it.exists()){
                offer(DataState.Success(it.toObject(User::class.java)))
            }
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose{
            cancel()
        }
    }
    //P2
    override suspend fun answerToInvitation(answer: Int): Flow<DataState<String>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!)
        ref.child("accept").setValue(FirebaseProvider.INVITE_ANSWER_EMPTY).addOnSuccessListener {
            ref.child("accept").setValue(answer).addOnSuccessListener {
                offer(DataState.Success("success"))
            }.addOnFailureListener { errorSetAnswer ->
                offer(DataState.Error(errorSetAnswer))
            }
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose{
            cancel()
        }
    }
    //P1 //**
    override suspend fun observeInvitationAnswer(userId: String): Flow<DataState<Int>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userId).child("accept")
        val listener = ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val answer = snapshot.getValue(Int::class.java)
                    if (answer!=null && (answer == FirebaseProvider.INVITE_ANSWER_ACCEPT || answer == FirebaseProvider.INVITE_ANSWER_DECLINE)){
                        offer(DataState.Success(answer))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        awaitClose {
            cancel()
        }
    }
    //P1
    override suspend fun createInvitationRoom(): Flow<DataState<String>> = callbackFlow {

        offer(DataState.Loading)
        val roomId = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).push().key
        val roomRef = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId!!).ref
        val children:HashMap<String, Any?> = HashMap()
        children["start"] = false
        children["questions-ready"] = false
        children["P1-username"] = userSharedPref.getUserPref().username
        children["P2-username"] = "waiting"
        children["P1-avatar"] = userSharedPref.getUserPref().avatar
        children["P2-avatar"] = -1
        children["P1-team"] = userSharedPref.getUserPref().team
        children["P2-team"] = -1
        children["P1-point"] = 0
        children["P2-point"] = 0
        children["P1-time"] = 0
        children["P2-time"] = 0
        children["quarter"] = 0
        children["P1-answer1"] = FirebaseProvider.ANSWER_EMPTY
        children["P1-answer2"] = FirebaseProvider.ANSWER_EMPTY
        children["P1-answer3"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer1"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer2"] = FirebaseProvider.ANSWER_EMPTY
        children["P2-answer3"] = FirebaseProvider.ANSWER_EMPTY
        roomRef.updateChildren(children).addOnSuccessListener {
            offer(DataState.Success(roomId))
            Log.i(TAG, "createRoom: success")
        }.addOnFailureListener {
            Log.i(TAG, "createRoom: error")
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P1
    override suspend fun setRoomId(roomId: String, userId: String): Flow<DataState<String>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userId).child("room-id")
        ref.setValue(roomId).addOnSuccessListener {
            offer(DataState.Success("roomId set"))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
    //P2 //**
    override suspend fun observeRoomId(): Flow<DataState<String>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!).child("room-id")
        val listener = ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   val result = snapshot.getValue(String::class.java)
                   if (result != null && result.isNotEmpty()){
                       offer(DataState.Success(result))
                   }
               }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        awaitClose {
            cancel()
        }
    }
    //P2
    override suspend fun joinToInvitationRoom(roomId: String): Flow<DataState<String>> = callbackFlow {

        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_ROOMS).child(roomId)
        ref.child("P2-username").get().addOnSuccessListener {
            if (it.exists() && it.getValue(String::class.java).equals("waiting")){
                it.ref.setValue(userSharedPref.getUserPref().username).addOnSuccessListener {
                    ref.child("P2-avatar").setValue(userSharedPref.getUserPref().avatar).addOnSuccessListener {
                        ref.child("P2-team").setValue(userSharedPref.getUserPref().team).addOnSuccessListener {
                            offer(DataState.Success(roomId))
                        }.addOnFailureListener { errorTeam ->
                            offer(DataState.Error(errorTeam))
                        }
                    }.addOnFailureListener { errorAvatar ->
                        offer(DataState.Error(errorAvatar))
                    }
                }.addOnFailureListener { errorUsername ->
                    offer(DataState.Error(errorUsername))
                }
            }
        }

        awaitClose{
            cancel()
        }
    }
    //P2 //?
    override suspend fun backToDefaultAfterDecline(): Flow<DataState<String>> = callbackFlow {

        setImEmpty()
        val ref = firebaseProvider.realTime.getReference(FirebaseProvider.REALTIME_USERS).child(userSharedPref.getUserPref().id!!).child("rival-id")
        ref.setValue("").addOnSuccessListener {
            offer(DataState.Success("success"))
        }.addOnFailureListener {
            offer(DataState.Error(it))
        }

        awaitClose {
            cancel()
        }
    }
}