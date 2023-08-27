package com.pose.flashcat.component.game

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.pose.flashcat.R.drawable.*
import java.util.*

enum class Game {

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    BASIC_REVIEW(
        gameName = "Basic review",
        description = "You will see a flashcard that contains a term. " +
                "You have to remember the definition of the term, and then check your answer. " +
                "After this, you choose how accurate your answer was.",
        icon = cards,
        minimumCardsCount = 2,
        game = { BasicReview() }
    ),

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    MULTIPLE_CHOICE(
        gameName = "Multiple choice",
        description = "You will see a definition, and multiple terms. Choose the term that corresponds " +
                "to the definition",
        icon = adjust,
        minimumCardsCount = 5,
        game = { MultipleChoice() }
    ),

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    MATCH_TILES(
        gameName = "Match tiles",
        description = "You will see tiles corresponding to different terms and definitions. " +
                "Match the terms to the correct definition.",
        icon = puzzle,
        minimumCardsCount = 5,
        game = { MatchTiles() }
    );

    val gameName: String
    val description: String
    val minimumCardsCount: Int
    val route: String = name.lowercase(Locale.ENGLISH)
    val getPainter: @Composable () -> Painter
    val compose: GameComposable

    constructor(
        gameName: String,
        description: String,
        @DrawableRes icon: Int,
        minimumCardsCount: Int,
        game: GameComposable
    ) {
        this.gameName = gameName
        this.description = description
        this.minimumCardsCount = minimumCardsCount
        this.getPainter = { painterResource(id = icon) }
        this.compose = game
    }

    constructor(
        gameName: String,
        description: String,
        icon: ImageVector,
        minimumCardsCount: Int,
        game: GameComposable
    ) {
        this.gameName = gameName
        this.description = description
        this.minimumCardsCount = minimumCardsCount
        this.getPainter = { rememberVectorPainter(icon) }
        this.compose = game
    }
}

val GAMES = Game.values().toList()
typealias GameComposable = @Composable GameContext.() -> Unit