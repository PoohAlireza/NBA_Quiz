package com.some_package.nbaquiz.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseProvider  {

    ///cloud firebase
    val QUESTIONS_COLLECTION_KEY = "questions"
    val OPTIONS_KEY = "options"
    val USERS_COLLECTION_KEY = "users"
    val ROOMS_COLLECTION_KEY = "rooms"
    //in game
    val HOST = 8
    val GUEST = 9
    val ANSWER_EMPTY = 2
    val ANSWER_CORRECT = 0
    val ANSWER_WRONG = 1
    val ANSWER_NO_TIME = 3
    //status
    val EMPTY = 13
    val IN_GAME = 14
    val ACCEPTED = 15
    val REJECTED = 16
    val WAITING = 17
    //invite
    val INVITE_ANSWER_EMPTY = 2
    val INVITE_ANSWER_ACCEPT = 0
    val INVITE_ANSWER_DECLINE = 1
    //realtimeDB
    val REALTIME_ROOMS = "rooms"
    val REALTIME_USERS = "users"
    val REALTIME_INVITES_ROOMS = "invite-rooms"

    val realTime: FirebaseDatabase = FirebaseDatabase.getInstance()
    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

}