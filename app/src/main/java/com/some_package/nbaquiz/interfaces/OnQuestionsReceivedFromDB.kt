package com.some_package.nbaquiz.interfaces

import com.some_package.nbaquiz.model.Question

interface OnQuestionsReceivedFromDB {
    fun onReceived(questions : List<Question>)
}