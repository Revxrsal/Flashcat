package com.pose.flashcat.component.deck

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pose.flashcat.R
import com.pose.flashcat.component.ConfirmationDialog
import com.pose.flashcat.component.RibbonCard
import com.pose.flashcat.component.color
import com.pose.flashcat.component.rememberBoolean
import com.pose.flashcat.db.entity.Deck
import com.pose.flashcat.db.entity.Flashcard
import com.pose.flashcat.db.entity.Tag
import com.pose.flashcat.model.DecksViewModel

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ListCardsInDeck(
    model: DecksViewModel,
    deck: Deck,
    controller: NavController
) {
    var display by rememberBoolean()
    Column {
        TopIcons(controller, deck, model)
        val cards = deck.cards
        Box(Modifier.fillMaxSize()) {
            if (cards.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "It's pretty quiet here.",
                        modifier = Modifier
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Click the + below to create a flashcard!",
                        modifier = Modifier
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                    items(cards) {
                        DisplayCard(
                            model = model,
                            deck = deck,
                            card = it,
                            controller = controller
                        )
                    }
                }
            }
            ExtendedFloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                text = { Text("New flashcard") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                onClick = { display = true },
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            )
        }
    }
    if (display) {
        Dialog(onDismissRequest = { display = false }) {
            var term by remember { mutableStateOf("") }
            var definition by remember { mutableStateOf("") }
            var tag by remember { mutableStateOf<Tag?>(null) }
            EditCard(
                controller = controller,
                deck = deck,
                term = term,
                onTermChanged = { term = it },
                definition = definition,
                onDefinitionChanged = { definition = it },
                tag = tag,
                onTagSelected = { tag = it },
                onDismissRequest = { display = false },
                onFinished = {
                    deck.add(model, Flashcard(term, definition, tag?.id))
                }
            )
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
private fun EditCard(
    controller: NavController,
    deck: Deck,
    term: String,
    onTermChanged: (String) -> Unit,
    definition: String,
    onDefinitionChanged: (String) -> Unit,
    tag: Tag?,
    onTagSelected: (Tag?) -> Unit,
    onDismissRequest: () -> Unit,
    onFinished: () -> Unit
) {
    var listTags by rememberBoolean()
    Surface(elevation = 5.dp) {
        val keyboard = LocalSoftwareKeyboardController.current
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = term,
                onValueChange = onTermChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text("Term") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboard?.hide()
                    }
                )
            )
            OutlinedTextField(
                value = definition,
                onValueChange = onDefinitionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                label = { Text("Definition") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboard?.hide()
                    }
                )
            )
            RibbonCard(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { listTags = true },
                ribbonColor = tag?.colorRGB?.color() ?: Color.DarkGray
            ) {
                Text(
                    text = tag?.name ?: "Select tag",
                    modifier = Modifier.padding(3.dp),
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                enabled = term.isNotBlank() && definition.isNotBlank(),
                modifier = Modifier
                    .padding(12.5.dp)
                    .fillMaxWidth(),
                onClick = {
                    onDismissRequest()
                    onFinished()
                }
            ) {
                Text("Done")
            }
        }
    }
    if (listTags) {
        Dialog(onDismissRequest = { listTags = false }) {
            TagSelector(
                navController = controller,
                deck = deck,
                tag = tag,
                onTagSelected = onTagSelected,
                onDismissRequest = { listTags = false }
            )
        }
    }
}

@Composable
fun TagSelector(
    navController: NavController,
    deck: Deck,
    tag: Tag?,
    onTagSelected: (Tag?) -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(elevation = 5.dp) {
        Column(modifier = Modifier.padding(10.dp)) {
            LazyColumn {
                item {
                    RibbonCard(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { onTagSelected(null) },
                        ribbonColor = Color.DarkGray
                    ) {
                        Row {
                            Text(
                                text = "None",
                                modifier = Modifier.padding(3.dp),
                                fontWeight = FontWeight.Medium
                            )
                            if (tag == null) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .align(CenterVertically)
                                )
                            }
                        }
                    }
                }
                items(deck.tags.values.toList()) {
                    RibbonCard(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { onTagSelected(it) },
                        ribbonColor = it.colorRGB.color()
                    ) {
                        Row {
                            Text(
                                text = it.name,
                                modifier = Modifier.padding(5.dp)
                            )
                            if (tag == it) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .align(CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    onDismissRequest()
                    navController.navigate("tags/${deck.id}")
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Manage tags")
            }
            Divider(modifier = Modifier.padding(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    onClick = {
                        onDismissRequest()
                        onTagSelected(tag)
                    }
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun DisplayCard(
    model: DecksViewModel,
    deck: Deck,
    card: Flashcard,
    controller: NavController
) {
    val tag = card.tag?.let { deck.tags[it] }
    var edit by rememberBoolean()
    var delete by rememberBoolean()
    RibbonCard(
        modifier = Modifier.padding(10.dp),
        ribbonColor = tag?.colorRGB?.color() ?: Color.DarkGray
    ) {
        Column {
            Text(
                modifier = Modifier.padding(6.dp),
                text = card.term,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                text = card.definition,
            )
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable { edit = true },
                    imageVector = Icons.Filled.Edit, contentDescription = "Edit"
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable { delete = true },
                    imageVector = Icons.Filled.Delete, contentDescription = "Delete"
                )
            }
        }
    }
    if (edit) {
        var term by remember { mutableStateOf(card.term) }
        var definition by remember { mutableStateOf(card.definition) }
        var nTag by remember { mutableStateOf(card.tag?.let { deck.tags[it] }) }
        Dialog(onDismissRequest = { edit = false }) {
            EditCard(
                controller = controller,
                deck = deck,
                term = term,
                onTermChanged = { term = it },
                definition = definition,
                onDefinitionChanged = { definition = it },
                tag = nTag,
                onTagSelected = { nTag = it },
                onDismissRequest = { edit = false },
                onFinished = {
                    card.term = term
                    card.definition = definition
                    card.tag = nTag?.id
                    model.update(deck)
                }
            )
        }
    }
    if (delete) {
        ConfirmationDialog(
            text = "Are you sure you want to delete this card?",
            close = { delete = false },
            modifier = Modifier.padding(10.dp),
            confirmed = { deck.remove(model, card) }
        )
    }
}

@Composable
private fun TopIcons(
    controller: NavController,
    deck: Deck,
    model: DecksViewModel
) {
    var clear by rememberBoolean()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { controller.navigateUp() }
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable { clear = true }
                )
                Icon(
                    painter = painterResource(id = R.drawable.tag),
                    contentDescription = "Tags",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable { controller.navigate("tags/${deck.id}") }
                )
            }
        }
    }
    if (clear) {
        ConfirmationDialog(
            text = "Are you sure you want to clear all cards in this deck? This action cannot be undone!",
            close = { clear = false },
            modifier = Modifier.padding(10.dp),
            confirmed = { deck.clearCards(model) }
        )
    }
}