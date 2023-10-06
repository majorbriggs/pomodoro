package com.majorbriggs.pomodoro.wear.data.db.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.util.Date

class DateConverter {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
