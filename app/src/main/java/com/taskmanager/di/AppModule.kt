package com.taskmanager.di

import android.app.Application
import android.content.Context
import com.taskmanager.data.local.notification.LocalNotificationManager
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
    fun provideApplicationContext(application: Application): Context = application.applicationContext
    
    @Provides
    @Singleton
    fun provideLocalNotificationManager(application: Application): LocalNotificationManager =
        LocalNotificationManager(application)
}
