package com.pose.flashcards.db

import androidx.room.*
import com.pose.flashcards.db.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Query("delete from flashcards")
    suspend fun deleteAllFlashcards()

    @Query("select * from flashcards")
    fun getFlashcards(): Flow<List<Flashcard>>

}