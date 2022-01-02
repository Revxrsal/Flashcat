package com.pose.flashcards.db

import com.pose.flashcards.db.entity.Flashcard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FlashcardsRepo @Inject constructor(private val dao: FlashcardsDao) {

    suspend fun addFlashcard(server: Flashcard) = dao.addFlashcard(server)

    suspend fun deleteAllFlashcards() = dao.deleteAllFlashcards()

    suspend fun deleteFlashcard(server: Flashcard) = dao.deleteFlashcard(server)

    suspend fun updateFlashcard(server: Flashcard) = dao.updateFlashcard(server)

    fun getFlashcards() = dao.getFlashcards().flowOn(Dispatchers.IO).conflate()

}