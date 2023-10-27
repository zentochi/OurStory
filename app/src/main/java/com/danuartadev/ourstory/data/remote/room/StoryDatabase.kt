package com.danuartadev.ourstory.data.remote.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.data.remote.response.StoryResponse


@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
