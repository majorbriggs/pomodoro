package com.majorbriggs.pomodoro.wear.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.majorbriggs.pomodoro.wear.data.model.SessionDto

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: SessionDto)

    @Query("SELECT * FROM sessions ORDER BY startDate DESC")
    suspend fun getAllSessions(): List<SessionDto>

    @Query("SELECT * FROM sessions ORDER BY startDate DESC")
    suspend fun getLatestSession(): SessionDto?
}
