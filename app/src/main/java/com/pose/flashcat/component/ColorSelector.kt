package com.pose.flashcat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pose.flashcat.R

@Composable
@ExperimentalFoundationApi
fun ColorSelector(
    color: Color,
    onColorSelected: (Color) -> Unit
) {
    LazyVerticalGrid(cells = GridCells.Fixed(4)) {
        items(PRESET_COLORS) {
            DisplayColor(
                color = it,
                onClick = { onColorSelected(it) },
                content = {
                    if (color == it)
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
                }
            )
        }
        item {
            ColorRGBSelector(
                initial = color,
                onColorSelected = onColorSelected
            )
        }
    }
}

@Composable
private fun ColorRGBSelector(initial: Color, onColorSelected: (Color) -> Unit) {
    var display by rememberBoolean()
    Button(
        modifier = Modifier
            .padding(10.dp)
            .sizeIn(
                minWidth = 40.dp,
                minHeight = 40.dp,
                maxWidth = 40.dp,
                maxHeight = 40.dp
            ),
        onClick = { display = true },
        colors = ButtonDefaults.buttonColors(initial),
        shape = CircleShape
    ) {
        Icon(painter = painterResource(id = R.drawable.eyedropper), contentDescription = "Custom")
    }
    if (display) {
        var red by remember { mutableStateOf(initial.red) }
        var green by remember { mutableStateOf(initial.green) }
        var blue by remember { mutableStateOf(initial.blue) }
        Dialog(onDismissRequest = { display = false }) {
            Surface(elevation = 5.dp) {
                Column {
                    Surface(
                        elevation = 5.dp,
                        modifier = Modifier
                            .padding(bottom = 10.dp, top = 15.dp, start = 10.dp, end = 10.dp)
                            .height(30.dp)
                            .fillMaxWidth(),
                        color = Color(red, green, blue),
                        shape = RoundedCornerShape(3.dp)
                    ) {}
                    Slider(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        value = red, onValueChange = { red = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red
                        )
                    )
                    Slider(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        value = green, onValueChange = { green = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Green,
                            activeTrackColor = Color.Green
                        )
                    )
                    Slider(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        value = blue, onValueChange = { blue = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(red = 20, green = 20, blue = 255),
                            activeTrackColor = Color(red = 20, green = 20, blue = 255)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(12.5.dp)
                                .weight(1f),
                            onClick = {
                                display = false
                            }) {
                            Text("Cancel")
                        }
                        Button(
                            modifier = Modifier
                                .padding(12.5.dp)
                                .weight(1f),
                            onClick = {
                                display = false
                                onColorSelected(Color(red, green, blue))
                            }) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayColor(
    color: Color,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(
        modifier = Modifier
            .padding(10.dp)
            .sizeIn(minWidth = 40.dp, minHeight = 40.dp, maxWidth = 40.dp, maxHeight = 40.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(color),
        shape = CircleShape
    ) {
        content()
    }
}

private val PRESET_COLORS = listOf(
    Color(244, 67, 54, 255),
    Color(233, 30, 99, 255),
    Color(156, 39, 176, 255),
    Color(103, 58, 183, 255),
    Color(63, 81, 181, 255),
    Color(33, 150, 243, 255),
    Color(3, 169, 244, 255),
    Color(0, 188, 212, 255),
    Color(0, 150, 136, 255),
    Color(76, 175, 80, 255),
    Color(139, 195, 74, 255),
    Color(205, 220, 57, 255),
    Color(255, 193, 7, 255),
    Color(255, 152, 0, 255),
    Color(255, 87, 34, 255)
)

fun Int.color() = Color(this)
