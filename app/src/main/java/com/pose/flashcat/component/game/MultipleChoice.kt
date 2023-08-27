package com.pose.flashcat.component.game

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pose.flashcat.db.entity.Flashcard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private fun GameContext.generateAnswers(card: Flashcard): MutableList<String> {
    val answers = mutableListOf<String>()
    with(LinkedList(deck.cards)) {
        remove(card)
        shuffle()
        answers += card.term
        answers += pop().term
        answers += pop().term
        answers += pop().term
        answers.shuffle()
    }
    return answers
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun GameContext.MultipleChoice() {
    var flashcard by remember { mutableStateOf(nextCard(null)) }
    DisplayFlashcard(
        card = flashcard,
        onCardChange = {
            flashcard = nextCard(skip = it)
            println("Flashcard: $flashcard")
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun GameContext.DisplayFlashcard(
    card: Flashcard,
    onCardChange: (Flashcard) -> Unit
) {
    val answers = remember(card) { generateAnswers(card) }
    var choseAnswer by remember(card) { mutableStateOf(false) }
    var selectedAnswer by remember(card) { mutableStateOf(-1) }
    AnimatedContent(targetState = card) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = it.definition,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .align(Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Divider(modifier = Modifier.padding(10.dp))
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(.5f)) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(2),
                    modifier = Modifier.align(Center)
                ) {
                    itemsIndexed(answers) { index, item ->
                        Choice(
                            text = item,
                            hasSelectedAnAnswer = choseAnswer,
                            correct = item == it.term,
                            wasSelected = selectedAnswer == index,
                            refreshCard = { onCardChange(it) },
                            onClick = {
                                if (!choseAnswer) {
                                    selectedAnswer = index
                                    choseAnswer = true
                                }
                            },
                            onWrong = {
                                it.recallLevel = it.recallLevel.previous()
                            },
                            onCorrect = {
                                it.recallLevel = it.recallLevel.next()
                            }
                        )
                    }
                }
            }
        }
    }
}

val Correct = Color(0, 95, 4, 255)
val Wrong = Color(126, 0, 0, 255)

@Composable
private fun Choice(
    text: String,
    correct: Boolean,
    hasSelectedAnAnswer: Boolean,
    wasSelected: Boolean,
    refreshCard: () -> Unit,
    onClick: () -> Unit,
    onCorrect: () -> Unit,
    onWrong: () -> Unit,
) {
    val borderAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    val surfaceColor = MaterialTheme.colors.surface
    val color = remember(text) { Animatable(surfaceColor) }
    val coroutine = rememberCoroutineScope()

    if (hasSelectedAnAnswer) {
        LaunchedEffect(wasSelected) {
            coroutine.launch {
                borderAlpha.animateTo(1f)
                delay(1000)
                color.animateTo(if (correct) Correct else Wrong)
                delay(1000)
            }.invokeOnCompletion {
                if (wasSelected) {
                    if (correct) onCorrect()
                    else onWrong()
                    refreshCard()
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .padding(10.dp)
            .height(60.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp),
        color = color.value,
        border = if (wasSelected) BorderStroke(width = 3.dp, color = Color.Gray.copy(
            alpha = borderAlpha.value
        )) else null
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(7.dp)
                    .align(Center),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
