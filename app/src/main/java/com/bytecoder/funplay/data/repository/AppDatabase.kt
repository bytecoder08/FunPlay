package com.bytecoder.funplay.data.repository

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Database(entities = [VideoEntity::class, Playlist::class, PlaylistItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun videoDao(): VideoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "funplay.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAll(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoEntity): Long

    @Delete
    suspend fun delete(video: VideoEntity)
}

