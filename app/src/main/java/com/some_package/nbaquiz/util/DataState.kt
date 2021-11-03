package com.some_package.nbaquiz.util

import java.lang.Exception

sealed class DataState<out R>{
    data class Success<out T>(val data: T?):DataState<T>()
    data class Error(val exception: Exception):DataState<Nothing>()
    data class Warning(val warning: String):DataState<Nothing>()
    object Loading:DataState<Nothing>()
}
