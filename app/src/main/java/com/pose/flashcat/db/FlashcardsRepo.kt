package com.pose.flashcat.db

import com.pose.flashcat.db.dao.DecksDao
import com.pose.flashcat.db.entity.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FlashcardsRepo @Inject constructor(
    private val decks: DecksDao
) {

    suspend fun add(deck: Deck) = decks.add(deck)

    suspend fun deleteAllDecks() = decks.deleteAllDecks()

    suspend fun delete(deck: Deck) = decks.delete(deck)

    suspend fun update(deck: Deck) = decks.update(deck)

    fun getDecks() = decks.getDecks().flowOn(Dispatchers.IO).conflate()

}
