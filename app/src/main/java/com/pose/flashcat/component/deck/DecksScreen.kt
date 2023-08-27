package com.pose.flashcat.component.deck

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pose.flashcat.component.ColorSelector
import com.pose.flashcat.component.ConfirmationDialog
import com.pose.flashcat.component.RibbonCard
import com.pose.flashcat.component.game.ListGames
import com.pose.flashcat.component.rememberBoolean
import com.pose.flashcat.db.entity.Deck
import com.pose.flashcat.model.DecksViewModel

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun DecksList(
    model: DecksViewModel,
    controller: NavController
) {
    val decks = model.decks.collectAsState().value
    var display by rememberBoolean()
    Box(Modifier.fillMaxSize()) {
        if (decks.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Let's get started!",
                    modifier = Modifier
                        .padding(10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Click the + below to create a new deck!",
                    modifier = Modifier
                        .padding(10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
        } else {
            Column {
                Text(
                    text = "My Decks",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .align(CenterHorizontally)
                )
                LazyColumn {
                    items(decks) {
                        DisplayDeck(
                            navController = controller,
                            deck = it,
                            onClick = {
                                controller.navigate("deck/${it.id}")
                            },
                            onDeleted = { model.delete(it) },
                            onUpdated = { name, color ->
                                it.name = name
                                it.color = color.toArgb()
                                model.update(it)
                            }
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            text = { Text("New deck") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = { display = true },
            icon = {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        )
    }
    if (display) {
        Dialog(onDismissRequest = { display = false }) {
            var name by remember { mutableStateOf("") }
            var color by remember { mutableStateOf(Color.Gray) }
            EditDeck(
                name = name,
                onNameChanged = { name = it },
                color = color,
                onColorChanged = { color = it },
                onDismissRequest = { display = false },
                onFinished = { model.add(Deck(name, color.toArgb())) }
            )
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun DisplayDeck(
    navController: NavController,
    deck: Deck,
    onClick: () -> Unit,
    onDeleted: () -> Unit,
    onUpdated: (String, Color) -> Unit
) {
    var edit by rememberBoolean()
    var delete by rememberBoolean()
    var review by rememberBoolean()
    RibbonCard(
        ribbonColor = Color(deck.color),
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = deck.name,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(10.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${deck.cards.size} card" + if (deck.cards.size == 1) "" else "s",
                modifier = Modifier.padding(10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                OutlinedButton(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        review = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.DarkGray
                    )
                ) { Text("Review") }
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { edit = true }
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { delete = true }
                    )
                }
            }
        }
    }
    if (edit) {
        Dialog(onDismissRequest = { edit = false }) {
            var name by remember { mutableStateOf(deck.name) }
            var color by remember { mutableStateOf(Color(deck.color)) }
            EditDeck(
                name = name,
                onNameChanged = { name = it },
                color = color,
                onColorChanged = { color = it },
                onFinished = { onUpdated(name, color) },
                onDismissRequest = { edit = false }
            )
        }
    }
    if (delete) {
        ConfirmationDialog(
            text = "Are you sure you want to delete this deck? This action cannot be undone!",
            close = { delete = false },
            confirmed = onDeleted,
            modifier = Modifier.padding(10.dp)
        )
    }
    if (review) {
        Dialog(onDismissRequest = { review = false }) {
            Surface(
                elevation = 5.dp,
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                ListGames(
                    deck = deck,
                    navigator = navController,
                    onDismissRequest = { review = false }
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun EditDeck(
    name: String,
    onNameChanged: (String) -> Unit,
    color: Color,
    onColorChanged: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    onFinished: () -> Unit
) {
    Surface(elevation = 5.dp) {
        val controller = LocalSoftwareKeyboardController.current
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text("Deck name") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        controller?.hide()
                    }
                )
            )
            ColorSelector(
                color = color,
                onColorSelected = onColorChanged
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(12.5.dp)
                        .weight(1f),
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
                Button(
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .padding(12.5.dp)
                        .weight(1f),
                    onClick = {
                        onDismissRequest()
                        onFinished()
                    }
                ) {
                    Text("Done")
                }
            }
        }
    }
}
