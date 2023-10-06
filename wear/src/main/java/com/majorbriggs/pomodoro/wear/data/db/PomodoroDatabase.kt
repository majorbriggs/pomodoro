package com.majorbriggs.pomodoro.wear.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.majorbriggs.pomodoro.wear.data.dao.SessionDao
import com.majorbriggs.pomodoro.wear.data.db.converter.DateConverter
import com.majorbriggs.pomodoro.wear.data.model.SessionDto

@Database(entities = [SessionDto::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class PomodoroDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    companion object {

        @Volatile
        private var INSTANCE: PomodoroDatabase? = null

        fun getDatabase(context: Context): PomodoroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PomodoroDatabase::class.java,
                    "pomodoro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
