package com.pose.flashcat.db.dao

import androidx.room.*
import com.pose.flashcat.db.entity.Deck
import com.pose.flashcat.db.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface DecksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Update
    suspend fun update(deck: Deck)

    @Query("delete from decks")
    suspend fun deleteAllDecks()

    @Query("select * from decks")
    fun getDecks(): Flow<List<Deck>>

}