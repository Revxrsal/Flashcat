package com.pose.flashcat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.pose.flashcat.component.MainNavigator
import com.pose.flashcat.model.DecksViewModel
import com.pose.flashcat.model.SettingsViewModel
import com.pose.flashcat.ui.theme.FlashcardsTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settings by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val flashcards by viewModels<DecksViewModel>()
            FlashcardsTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigator(
                        model = flashcards,
                        settings = settings
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        settings.save()
    }
}
