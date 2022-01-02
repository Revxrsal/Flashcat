package com.pose.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pose.flashcards.model.FlashcardsViewModel
import com.pose.flashcards.ui.theme.FlashcardsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardsTheme(darkTheme = true) {
                val model by viewModels<FlashcardsViewModel>()
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainView(model = model)
                }
            }
        }
    }
}

@Composable
fun MainView(model: FlashcardsViewModel) {

}
