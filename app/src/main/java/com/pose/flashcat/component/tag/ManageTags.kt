package com.pose.flashcat.component.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pose.flashcat.component.ConfirmationDialog
import com.pose.flashcat.component.RibbonCard
import com.pose.flashcat.component.rememberBoolean
import com.pose.flashcat.db.entity.Deck
import com.pose.flashcat.db.entity.Tag
import com.pose.flashcat.model.DecksViewModel

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ManageTagsScreen(
    deck: Deck,
    model: DecksViewModel,
    controller: NavController
) {
    var display by rememberBoolean()
    Column {

        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .padding(10.dp)
                .clickable { controller.navigateUp() }

        )
        Box(Modifier.fillMaxSize()) {
            val tags = deck.tags
            if (tags.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No tags yet! :(",
                        modifier = Modifier
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Click the + below to add one!",
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
                        text = "Tags",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    LazyColumn {
                        items(tags.values.toList()) {
                            DisplayTag(
                                tag = it,
                                onDeleted = { deck.remove(model, it) },
                                onUpdated = { name, color ->
                                    it.name = name
                                    it.colorRGB = color.toArgb()
                                    model.update(deck)
                                }
                            )
                        }
                    }
                }
            }
            ExtendedFloatingActionButton(
                text = { Text("New tag") },
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                onClick = { display = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            )
        }
        if (display) {
            Dialog(onDismissRequest = { display = false }) {
                var name by remember { mutableStateOf("") }
                var color by remember { mutableStateOf(Color.DarkGray) }
                EditTag(
                    name = name,
                    onNameChanged = { name = it },
                    color = color,
                    onColorChanged = { color = it },
                    onFinished = {
                        deck.add(model, Tag(name, color.toArgb()))
                    },
                    onDismissRequest = {
                        display = false
                    }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun DisplayTag(
    tag: Tag,
    onDeleted: () -> Unit,
    onUpdated: (String, Color) -> Unit
) {
    var edit by rememberBoolean()
    var delete by rememberBoolean()
    RibbonCard(
        ribbonColor = Color(tag.colorRGB),
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag.name,
                modifier = Modifier
                    .padding(3.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(3.dp)
                    .clickable { edit = true }
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(3.dp)
                    .clickable { delete = true }
            )
        }
    }
    if (edit) {
        Dialog(onDismissRequest = { edit = false }) {
            var name by remember { mutableStateOf(tag.name) }
            var color by remember { mutableStateOf(Color(tag.colorRGB)) }
            EditTag(
                name = name,
                onNameChanged = { name = it },
                color = color,
                onColorChanged = { color = it },
                onFinished = {
                    onUpdated(name, color)
                },
                onDismissRequest = {
                    edit = false
                }
            )
        }
    }
    if (delete) {
        ConfirmationDialog(
            text = "Are you sure you want to delete this tag? This action cannot be undone!",
            close = { delete = false },
            confirmed = onDeleted,
            modifier = Modifier.padding(10.dp)
        )
    }
}
