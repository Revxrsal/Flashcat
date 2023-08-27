package com.pose.flashcat.component.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.pose.flashcat.component.ColorSelector

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun EditTag(
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
                label = { Text("Name") },
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
