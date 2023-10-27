package com.danuartadev.ourstory.data.remote.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.danuartadev.ourstory.data.remote.response.ListStoryItem


@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM list_story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>


    @Query("DELETE FROM list_story")
    suspend fun deleteAll()
}