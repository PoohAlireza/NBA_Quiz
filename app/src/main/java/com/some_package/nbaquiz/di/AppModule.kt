package com.some_package.nbaquiz.di

import androidx.fragment.app.FragmentFactory
import com.some_package.nbaquiz.util.MyFragmentFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Qualifier
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideMyFragmentFactory():MyFragmentFactory{
        return MyFragmentFactory()
    }

    @Singleton
    @Authors
    @Provides
    fun provideAuthors():Array<String>{
        return arrayOf("Michael Jordan","LeBron James","Tim Duncan")
    }

    @Singleton
    @Texts
    @Provides
    fun provideTexts():Array<String>{
        return arrayOf("“I can accept failure, everyone fails at something. But I can’t accept not trying.”","“Nothing is given. Everything is earned.”","“Good, better, best. Never let it rest. Until your good is better and your better is best.”")
    }










    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Authors

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Texts

}