package com.majorbriggs.pomodoro.wear.data.di

import android.app.Application
import com.majorbriggs.pomodoro.wear.data.dao.SessionDao
import com.majorbriggs.pomodoro.wear.data.db.PomodoroDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.InstallIn

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideSessionDatabase(application: Application): PomodoroDatabase {
        return PomodoroDatabase.getDatabase(application)
    }

    @Provides
    fun provideSessionDao(sessionDatabase: PomodoroDatabase): SessionDao {
        return sessionDatabase.sessionDao()
    }
}
