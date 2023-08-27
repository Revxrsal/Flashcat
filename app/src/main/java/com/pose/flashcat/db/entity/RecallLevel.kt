package com.pose.flashcat.db.entity

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

enum class RecallLevel(
    val levelName: String = "",
    val color: Color,
    val displayLabel: Boolean = false,
    @FloatRange(from = 0.0, to = 1.0) val chance: Double,
    val previous: () -> RecallLevel,
    val next: () -> RecallLevel
) {

    NOT_AT_ALL(
        levelName = "Not at all",
        color = Color(99, 0, 0, 255),
        displayLabel = true,
        chance = 0.4,
        previous = { NOT_AT_ALL },
        next = { BAD }
    ),

    BAD(
        levelName = "Not a lot",
        color = Color(255, 87, 34, 255),
        chance = 0.3,
        previous = { NOT_AT_ALL },
        next = { OK }
    ),

    OK(
        levelName = "OK",
        color = Color(116, 141, 88, 255),
        displayLabel = true,
        chance = 0.15,
        previous = { BAD },
        next = { GOOD }
    ),

    GOOD(
        levelName = "Good",
        color = Color(76, 175, 80, 255),
        chance = 0.1,
        previous = { OK },
        next = { PERFECT }
    ),

    PERFECT(
        levelName = "Perfectly",
        color = Color(3, 169, 244, 255),
        displayLabel = true,
        chance = 0.05,
        previous = { GOOD },
        next = { PERFECT }
    )
}

val recallLevels = RecallLevel.values().toList()

fun List<RecallLevel>.randomByWeight(): RecallLevel {
    var index = 0
    var r = Math.random()
    while (index < size - 1) {
        r -= this[index].chance
        if (r <= 0.0) break
        ++index
    }
    return this[index]
}