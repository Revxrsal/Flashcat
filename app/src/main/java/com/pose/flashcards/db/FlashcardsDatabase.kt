package com.pose.flashcards.db

import androidx.room.BuiltInTypeConverters
import androidx.room.BuiltInTypeConverters.State
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pose.flashcards.db.entity.Flashcard

@Database(
    entities = [Flashcard::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    builtInTypeConverters = BuiltInTypeConverters(
        uuid = State.ENABLED,
        enums = State.DISABLED
    )
)
abstract class FlashcardsDatabase : RoomDatabase() {

    abstract fun dao(): FlashcardsDao

}