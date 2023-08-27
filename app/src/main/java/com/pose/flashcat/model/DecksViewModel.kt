package com.pose.flashcat.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pose.flashcat.db.FlashcardsRepo
import com.pose.flashcat.db.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DecksViewModel @Inject constructor(
    private val repository: FlashcardsRepo
) : ViewModel() {

    private val _decks = MutableStateFlow<List<Deck>>(mutableListOf())

    val decks = _decks.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDecks().distinctUntilChanged().collect {
                _decks.value = it
            }
        }
    }

    fun add(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.add(deck)
    }

    fun delete(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(deck)
    }

    fun update(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(deck)
    }

    fun deleteAllDecks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllDecks()
    }
}
