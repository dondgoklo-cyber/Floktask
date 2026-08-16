package com.taskmanager.di

import coil.ImageLoader
import com.taskmanager.data.remote.AuthService
import com.taskmanager.data.remote.FirebaseService
import com.taskmanager.image.ImageLoaderFactory
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
    fun provideImageLoader(factory: ImageLoaderFactory): ImageLoader = factory.create()

    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService = FirebaseService()

    @Provides
    @Singleton
    fun provideAuthService(firebaseService: FirebaseService): AuthService =
        AuthService(firebaseService)
}
