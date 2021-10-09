package com.some_package.nbaquiz.interfaces

import com.some_package.nbaquiz.model.Question

interface OnQuestionsReceivedFromRoom {
    fun onFromRooms(questions : List<Question>)
}