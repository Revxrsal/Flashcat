package com.pose.flashcat.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pose.flashcat.db.entity.Flashcard
import com.pose.flashcat.db.entity.Tag
import java.util.*

class Converters {
    private val gson = Gson()
    private val tagsListType = object : TypeToken<Map<UUID, Tag>>() {}.type
    private val cardsListType = object : TypeToken<List<Flashcard>>() {}.type

    @TypeConverter
    fun tagsToString(tags: Map<UUID, Tag>): String = gson.toJson(tags)

    @TypeConverter
    fun stringToTags(string: String): Map<UUID, Tag> = gson.fromJson(string, tagsListType)

    @TypeConverter
    fun flashcardsToString(flashcards: List<Flashcard>): String = gson.toJson(flashcards)

    @TypeConverter
    fun stringToFlashcards(string: String): List<Flashcard> = gson.fromJson(string, cardsListType)

}