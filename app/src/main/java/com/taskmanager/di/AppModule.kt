package com.taskmanager.di

import android.content.Context
import coil.ImageLoader
import com.taskmanager.data.remote.AuthService
import com.taskmanager.data.remote.FirebaseService
import com.taskmanager.haptic.HapticManager
import com.taskmanager.image.ImageLoaderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHapticManager(@ApplicationContext context: Context): HapticManager =
        HapticManager(context)

    @Provides
    @Singleton
    fun provideImageLoader(factory: ImageLoaderFactory): ImageLoader = factory.create()

    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService = FirebaseService()

    @Provides
    @Singleton
    fun provideExchangeRateProvider(): com.taskmanager.domain.finance.ExchangeRateProvider =
        com.taskmanager.data.repository.LocalExchangeRateProvider()
}
