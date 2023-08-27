package com.pose.flashcat.db.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.*

class Tag(
    var name: String = "None",
    var colorRGB: Int = Color.Gray.toArgb()
) {

    val id: UUID = UUID.randomUUID()

}