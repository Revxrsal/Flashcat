package com.pose.flashcards.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "flashcards")
class Flashcard(
    val name: String
) {

    @PrimaryKey
    var id: UUID = UUID.randomUUID()

}