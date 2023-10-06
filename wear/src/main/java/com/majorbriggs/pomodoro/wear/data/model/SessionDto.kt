package com.majorbriggs.pomodoro.wear.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sessions")
data class SessionDto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: Date
)
