package com.pose.flashcat.db.entity

import java.util.*

data class Flashcard(
    var term: String,
    var definition: String,
    var tag: UUID? = null,
    var recallLevel: RecallLevel = RecallLevel.NOT_AT_ALL
)
