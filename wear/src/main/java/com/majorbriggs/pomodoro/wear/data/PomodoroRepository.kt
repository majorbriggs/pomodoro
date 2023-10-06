package com.majorbriggs.pomodoro.wear.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
) {

    suspend fun startCountDown(isRunning: Boolean) {
    }
}
