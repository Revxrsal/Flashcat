package com.pose.flashcat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pose.flashcat.component.deck.DecksList
import com.pose.flashcat.component.deck.ListCardsInDeck
import com.pose.flashcat.component.game.GAMES
import com.pose.flashcat.component.game.GameScreenLayout
import com.pose.flashcat.component.tag.ManageTagsScreen
import com.pose.flashcat.db.entity.Deck
import com.pose.flashcat.model.DecksViewModel
import com.pose.flashcat.model.SettingsViewModel

val deckArg = navArgument("deck") { type = NavType.StringType }

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MainNavigator(
    model: DecksViewModel,
    settings: SettingsViewModel
) {
    val navigator = rememberNavController()
    val decks = model.decks.collectAsState().value
    NavHost(navController = navigator, startDestination = "decks") {
        composable("decks") {
            DecksList(model = model, controller = navigator)
        }
        composable("deck/{deck}", arguments = listOf(deckArg)) {
            val deck = it.getDeck(decks)
            ListCardsInDeck(model = model, deck = deck, controller = navigator)
        }
        composable("tags/{deck}", arguments = listOf(deckArg)) {
            val deck = it.getDeck(decks)
            ManageTagsScreen(deck = deck, model = model, controller = navigator)
        }
        for (game in GAMES) {
            composable("games/${game.route}/{deck}", arguments = listOf(deckArg)) {
                val deck = it.getDeck(decks)
                GameScreenLayout(model, game, deck, navigator)
            }
        }
    }
}


@Composable
private fun NavBackStackEntry.getDeck(decks: List<Deck>): Deck {
    return decks.first { deck -> deck.id.toString() == getString("deck") }
}


private fun NavBackStackEntry.getString(key: String): String {
    return checkNotNull(arguments?.getString(key)) { "Parameter '$key' not specified!" }
}