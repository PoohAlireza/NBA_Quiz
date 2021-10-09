package com.some_package.nbaquiz.di

import com.some_package.nbaquiz.interfaces.OnEditProfileClicked
import com.some_package.nbaquiz.interfaces.OnGameKindPageSelected
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterfaceModule {

    @Singleton
    @Provides
    fun provideOnGameKindPageSelected():OnGameKindPageSelected?{
        return null
    }

    @Singleton
    @Provides
    fun provideOnEditProfileClicked():OnEditProfileClicked?{
        return null
    }

}