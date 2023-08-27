package com.pose.flashcat.component.game

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pose.flashcat.R
import com.pose.flashcat.component.rememberBoolean
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@ExperimentalAnimationApi
@Composable
fun GameContext.MatchTiles() {

    val cards = remember(this) {
        val size = (5 + Random.nextInt(1, 3)).coerceAtMost(deck.cards.size)
        val c = deck.cards.toMutableList()
        c.sortBy { it.recallLevel.ordinal }
        c.subList(0, size).also { it.shuffle() }
    }

    val terms = remember {
        cards.map { it.term }.also { (it as MutableList<String>) }
    }

    val definitions = remember {
        cards.map { it.definition }.also { (it as MutableList<String>).shuffle() }
    }

    val solved = remember { mutableStateMapOf<Int, Int>() }

    var selectedTerm by remember(this) { mutableStateOf(-1) }
    var selectedDef by remember(this) { mutableStateOf(-1) }

    var isRenderingError by rememberBoolean()

    AnimatedVisibility(
        visible = solved.size >= cards.size,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        GameFinished()
    }
    AnimatedVisibility(
        visible = solved.size < cards.size,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                itemsIndexed(terms) { index, item ->
                    Tile(
                        text = item,
                        isRenderingError = isRenderingError,
                        selected = selectedTerm == index,
                        onSelected = { selectedTerm = if (it) index else -1 },
                        activeColor = Color(63, 81, 181, 255),
                        visible = !solved.containsKey(index),
                        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                itemsIndexed(definitions) { index, item ->
                    Tile(
                        text = item,
                        isRenderingError = isRenderingError,
                        selected = selectedDef == index,
                        onSelected = { selectedDef = if (it) index else -1 },
                        visible = !solved.containsValue(index),
                        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
                    )
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    if (selectedDef != -1 && selectedTerm != -1) {
        val card = cards[selectedTerm]
        if (card.definition == definitions[selectedDef]) {
            card.recallLevel = card.recallLevel.next()
            isRenderingError = false
            solved[selectedTerm] = selectedDef
            selectedDef = -1
            selectedTerm = -1
        } else {
            isRenderingError = true
            card.recallLevel = card.recallLevel.previous()
            LaunchedEffect(Unit) {
                scope.launch {
                    delay(1000)
                    isRenderingError = false
                    selectedDef = -1
                    selectedTerm = -1
                }
            }
        }
    }
}

@Composable
private fun GameContext.GameFinished() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(15.dp),
                text = "Woo hoo!",
                fontSize = 37.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier
                    .padding(15.dp),
                text = "You matched all cards correctly",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Image(
                modifier = Modifier
                    .padding(15.dp)
                    .size(175.dp),
                painter = painterResource(id = R.drawable.tada),
                contentDescription = "Finished game"
            )
            Button(
                onClick = { back() },
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun Tile(
    text: String,
    isRenderingError: Boolean,
    selected: Boolean,
    onSelected: (Boolean) -> Unit,
    visible: Boolean = true,
    activeColor: Color = Color(103, 58, 183, 255),
    inactiveColor: Color = Color.DarkGray,
    exit: ExitTransition
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = remember(text) { Animatable(inactiveColor) }
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = visible,
        exit = exit
    ) {
        LaunchedEffect(isRenderingError, selected) {
            if (isRenderingError && selected)
                color.animateTo(Color(121, 0, 0, 255))
            else
                color.animateTo(if (selected) activeColor else inactiveColor)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(6.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (!isRenderingError && visible) onSelected(!selected)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color.value)
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}