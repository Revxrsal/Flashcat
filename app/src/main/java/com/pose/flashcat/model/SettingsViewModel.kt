package com.pose.flashcat.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    var darkTheme by mutableStateOf(true)

    private val dataFile = File(context.dataDir, "preferences.json")
        .apply { createNewFile() }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val text = dataFile.readText()
            with(JSONObject(if (text.isNotEmpty()) text else "{}")) {
                launch(Dispatchers.Main) {
                    parse()
                }
            }
        }
    }

    private fun JSONObject.parse() {
        darkTheme = optBoolean("darkTheme", true)
    }

    private fun toJson() = JSONObject().apply {
        put("darkTheme", darkTheme)
    }

    fun save(): Job = viewModelScope.launch(Dispatchers.IO) {
        dataFile.writeText(toJson().toString())
    }
}