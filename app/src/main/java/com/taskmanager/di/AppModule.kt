package com.taskmanager.di

import com.taskmanager.data.remote.AuthService
import com.taskmanager.data.remote.FirebaseService
import com.taskmanager.domain.nlp.EisenhowerClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService = FirebaseService()

    @Provides
    @Singleton
    fun provideAuthService(firebaseService: FirebaseService): AuthService =
        AuthService(firebaseService)

    @Provides
    @Singleton
    fun provideEisenhowerClassifier(): EisenhowerClassifier = EisenhowerClassifier()
}
