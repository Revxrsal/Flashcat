package com.pose.flashcat.component.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.pose.flashcat.R
import com.pose.flashcat.component.ConfirmationDialog
import com.pose.flashcat.component.rememberBoolean
import com.pose.flashcat.db.entity.*
import com.pose.flashcat.model.DecksViewModel
import kotlinx.coroutines.Job

@Composable
fun ListGames(
    deck: Deck,
    navigator: NavController,
    onDismissRequest: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { onDismissRequest() }
            )
            Text(
                text = "Choose a game",
                modifier = Modifier.padding(10.dp)
            )
        }
        LazyColumn(
            Modifier
                .padding(3.dp)
        ) {
            items(GAMES) {
                DisplayGame(
                    onDismissRequest = onDismissRequest,
                    navigator = navigator,
                    game = it,
                    deck = deck
                )
            }
        }
    }
}

@Composable
private fun DisplayGame(
    onDismissRequest: () -> Unit,
    navigator: NavController,
    game: Game,
    deck: Deck
) {
    var noCardsYet by rememberBoolean()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clip(RoundedCornerShape(3.dp))
            .clickable {
                if (deck.cards.size < game.minimumCardsCount)
                    noCardsYet = true
                else {
                    onDismissRequest()
                    navigator.navigate("games/${game.route}/${deck.id}")
                }
            },
        color = Color.DarkGray,
        shape = RoundedCornerShape(3.dp)
    ) {
        Row {
            Icon(
                painter = game.getPainter(),
                contentDescription = game.gameName,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = game.gameName,
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
    if (noCardsYet) {
        NoCardsYet(
            minimum = game.minimumCardsCount,
            onDismissRequest = { noCardsYet = false },
            onClick = { navigator.navigate("deck/${deck.id}") }
        )
    }
}

@Composable
private fun NoCardsYet(
    minimum: Int,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .padding(20.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "You need at least $minimum flashcard${if (minimum == 1) "" else "s"} to practice!",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Row {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        onClick = {
                            onDismissRequest()
                            onClick()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                    ) {
                        Text(text = "Add cards")
                    }
                }
            }
        }
    }
}

data class GameContext(
    val model: DecksViewModel,
    val deck: Deck,
    val navigator: NavController
) {
    private fun update() = model.update(deck)

    fun back() = navigator.navigateUp()

    /**
     * We implement the totally not scientific spaced-repetition
     * here.
     */
    fun nextCard(skip: Flashcard?): Flashcard {
        val map = mutableMapOf<RecallLevel, MutableList<Flashcard>>()
        deck.cards.forEach {
            if (skip != it)
                map.getOrPut(it.recallLevel) { mutableListOf() }.add(it)
        }
        val level = recallLevels.randomByWeight()
        return map[level]?.randomOrNull() ?: nextCard(skip)
    }
}

@Composable
fun GameScreenLayout(
    model: DecksViewModel,
    game: Game,
    deck: Deck,
    navigator: NavHostController
) {
    var exit by rememberBoolean()
    var howToPlay by rememberBoolean()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Exit game",
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterStart)
                        .clickable { exit = true }
                )
                Icon(
                    painter = painterResource(id = R.drawable.help),
                    contentDescription = "How to play",
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterEnd)
                        .clickable { howToPlay = true }
                )
            }
        }
        val context = GameContext(model, deck, navigator)
        game.compose(context)
    }
    if (exit) {
        ConfirmationDialog(
            text = "Are you sure you want to leave the game?",
            close = { exit = false },
            modifier = Modifier.padding(10.dp),
            confirmed = {
                model.update(deck)
                navigator.navigateUp()
            }
        )
    }
    if (howToPlay) {
        HowToPlay(
            description = game.description,
            onDismissRequest = { howToPlay = false }
        )
    }
}

@Composable
private fun HowToPlay(description: String, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .padding(20.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 6.dp,
                            bottom = 2.dp,
                            start = 6.dp,
                            end = 6.dp
                        )
                        .fillMaxWidth(),
                    text = "How to play",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = description,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Row {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        onClick = onDismissRequest
                    ) {
                        Text(text = "Got it")
                    }
                }
            }
        }
    }
}
