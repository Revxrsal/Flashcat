package com.pose.flashcat.db

import androidx.room.BuiltInTypeConverters
import androidx.room.BuiltInTypeConverters.State
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pose.flashcat.db.dao.DecksDao
import com.pose.flashcat.db.entity.Deck

@Database(
    entities = [Deck::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [Converters::class],
    builtInTypeConverters = BuiltInTypeConverters(
        uuid = State.ENABLED,
        enums = State.ENABLED
    )
)
abstract class FlashcardsDatabase : RoomDatabase() {

    abstract fun decks(): DecksDao

}