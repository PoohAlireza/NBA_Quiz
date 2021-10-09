package com.some_package.nbaquiz.di

import com.some_package.nbaquiz.firebase.FirebaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {


    @Singleton
    @Provides
    fun provideFirebaseProvider():FirebaseProvider{
        return FirebaseProvider()
    }

}