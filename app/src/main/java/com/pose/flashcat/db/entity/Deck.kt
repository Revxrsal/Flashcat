package com.pose.flashcat.db.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pose.flashcat.model.DecksViewModel
import java.util.*

@Entity(tableName = "decks")
class Deck(
    var name: String,
    var color: Int,
//    var rtl: Boolean = false,
    val tags: MutableMap<UUID, Tag> = mutableMapOf(),
    val cards: MutableList<Flashcard> = mutableListOf()
) {

    @PrimaryKey
    var id: UUID = UUID.randomUUID()

    fun add(model: DecksViewModel, tag: Tag) = update(model) {
        tags[tag.id] = tag
    }

    fun add(model: DecksViewModel, card: Flashcard) = update(model) {
        cards.add(card)
    }

    fun remove(model: DecksViewModel, tag: Tag) = update(model) {
        tags.remove(tag.id)
    }

    fun remove(model: DecksViewModel, card: Flashcard) = update(model) {
        cards.remove(card)
    }

    fun clearCards(model: DecksViewModel) = update(model) {
        cards.clear()
    }

    fun clearTags(model: DecksViewModel) = update(model) {
        tags.clear()
    }

    fun putColor(model: DecksViewModel, color: Color) = update(model) {
        this.color = color.toArgb()
    }

    private inline fun update(model: DecksViewModel, update: () -> Unit) {
        update()
        model.update(this)
    }
//
//    val textStyle
//        get() = TextStyle(
//            textDirection = if (rtl)
//                TextDirection.ContentOrRtl
//            else
//                TextDirection.ContentOrLtr
//        )

}