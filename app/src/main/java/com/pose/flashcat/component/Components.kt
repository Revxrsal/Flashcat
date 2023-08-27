package com.pose.flashcat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun RibbonCard(
    modifier: Modifier = Modifier,
    ribbonColor: Color = Color.Yellow,
    backgroundColor: Color = Color.DarkGray,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ribbonColor)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    text: String,
    close: () -> Unit,
    confirmed: () -> Unit
) {
    Dialog(
        onDismissRequest = close,
    ) {
        Card(
            modifier = Modifier
                .padding(20.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column {
                Text(
                    modifier = modifier,
                    text = text,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Row {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        onClick = close
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        onClick = {
                            close()
                            confirmed()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                    ) {
                        Text(text = "Yes")
                    }
                }
            }
        }
    }
}

@Composable
fun rememberBoolean(value: Boolean = false) = remember { mutableStateOf(value) }