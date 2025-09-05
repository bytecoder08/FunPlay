package com.bytecoder.funplay.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.model.PlaylistItem

@Database(entities = [Playlist::class, PlaylistItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "funplay.db"
                ).build().also { INSTANCE = it }
            }
    }
}
