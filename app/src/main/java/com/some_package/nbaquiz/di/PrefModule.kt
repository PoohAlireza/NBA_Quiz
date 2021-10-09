package com.some_package.nbaquiz.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.some_package.nbaquiz.preferences.RegisterSharedPref
import com.some_package.nbaquiz.preferences.UserSharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefModule {

    @Singleton
    @UserPref
    @Provides
    fun provideSharedPrefForUser(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(UserSharedPref.USER_PREF,Context.MODE_PRIVATE)
    }



    @Singleton
    @RegisterPref
    @Provides
    fun provideSharedPrefForRegister(@ApplicationContext context: Context ):SharedPreferences{
        return context.getSharedPreferences(RegisterSharedPref.REGISTER_PREF,Context.MODE_PRIVATE)
    }




    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UserPref

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RegisterPref

}