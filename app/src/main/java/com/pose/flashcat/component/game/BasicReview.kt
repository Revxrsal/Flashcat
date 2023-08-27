package com.pose.flashcat.component.game

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pose.flashcat.db.entity.Flashcard
import com.pose.flashcat.db.entity.RecallLevel
import com.pose.flashcat.db.entity.recallLevels

@Composable
@ExperimentalAnimationApi
@ExperimentalMaterialApi
fun GameContext.BasicReview() {
    var card by remember { mutableStateOf(nextCard(skip = null)) }
    DisplayCard(
        card = card,
        onRefresh = { card = nextCard(card) }
    )
}

@Composable
@ExperimentalAnimationApi
@ExperimentalMaterialApi
fun DisplayCard(
    card: Flashcard,
    onRefresh: () -> Unit
) {
    AnimatedContent(targetState = card) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(BottomCenter)
            ) {
                var face by remember { mutableStateOf(CardFace.Front) }
                FlipCard(
                    card = it,
                    face = face,
                    onClick = { face = face.next }
                )
                Divider(modifier = Modifier.padding(6.dp))
                Box(modifier = Modifier.weight(.5f)) {
                    RatingButtons(
                        hasRevealed = face == CardFace.Back,
                        onSwitch = { face = face.next },
                        onClick = { level ->
                            card.recallLevel = level
                            onRefresh()
                        }
                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun RatingButtons(
    hasRevealed: Boolean,
    onSwitch: () -> Unit,
    onClick: (RecallLevel) -> Unit
) {
    AnimatedVisibility(
        visible = !hasRevealed,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Button(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            onClick = onSwitch,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text("Reveal answer")
        }
    }
    AnimatedVisibility(
        visible = hasRevealed,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column {
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                onClick = onSwitch
            ) {
                Text("Hide answer")
            }
            Text(
                text = "How well did you recall that?",
                modifier = Modifier.padding(6.dp)
            )
            Row {
                for (level in recallLevels) {
                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            modifier = Modifier
                                .padding(5.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = level.color),
                            onClick = { onClick(level) }
                        ) {
                            Text(
                                text = (level.ordinal + 1).toString(),
                                textAlign = TextAlign.Center
                            )
                        }
                        if (level.displayLabel) {
                            Text(
                                text = level.levelName,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(4.8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterialApi
private fun ColumnScope.FlipCard(
    card: Flashcard,
    face: CardFace,
    onClick: () -> Unit
) {
    FlipCard(
        ribbon = card.recallLevel.color,
        cardFace = face,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(10.dp)
            .weight(1f),
//            .height(300.dp),
        back = {
            Text(
                text = card.definition,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Center),
                textAlign = TextAlign.Center
            )
        },
        front = {
            Text(
                text = card.term,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Center),
                textAlign = TextAlign.Center
            )
        },
    )
}

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@ExperimentalMaterialApi
@Composable
private fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    back: @Composable (BoxScope.() -> Unit) = {},
    front: @Composable (BoxScope.() -> Unit) = {},
    ribbon: Color,
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )
    Box(
        modifier = modifier
            .background(color = ribbon, shape = MaterialTheme.shapes.medium)
    ) {

        Card(
            onClick = { onClick(cardFace) },
            modifier = Modifier.graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            },
        ) {
            if (rotation.value <= 90f) {
                Box(modifier = Modifier.fillMaxSize()) {
                    front()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                ) {
                    back()
                }
            }
        }
    }
}
