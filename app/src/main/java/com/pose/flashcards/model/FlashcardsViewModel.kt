package com.pose.flashcards.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pose.flashcards.db.FlashcardsRepo
import com.pose.flashcards.db.entity.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val repository: FlashcardsRepo
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())

    val flashcards = _flashcards.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFlashcards().distinctUntilChanged().collect {
                _flashcards.value = it
            }
        }
    }

    fun addFlashcard(entity: Flashcard): Job {
        return viewModelScope.launch { repository.addFlashcard(entity) }
    }

    fun deleteFlashcard(entity: Flashcard): Job {
        return viewModelScope.launch { repository.deleteFlashcard(entity) }
    }

    fun updateFlashcard(entity: Flashcard): Job {
        return viewModelScope.launch { repository.updateFlashcard(entity) }
    }

    fun deleteAllFlashcards(): Job {
        return viewModelScope.launch { repository.deleteAllFlashcards() }
    }
}
