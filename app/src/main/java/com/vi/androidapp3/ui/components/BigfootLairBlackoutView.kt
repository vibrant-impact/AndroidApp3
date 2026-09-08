package com.vi.androidapp3.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import kotlinx.coroutines.delay

@Composable
fun BigfootLairBlackoutView(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val opacity = remember { Animatable(0f) }
    val messageOpacity = remember { Animatable(0f) }
    val finalMessageOpacity = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundManager.shared.play(GameSound.BLACKOUT_RUMBLE, 0.85f)
        opacity.animateTo(1f, tween(2000))
        delay(800)
        messageOpacity.animateTo(1f, tween(800))
        delay(1900)
        finalMessageOpacity.animateTo(1f, tween(1800))
        delay(2500)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(opacity.value)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "A low rumble echoes through the cave.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(messageOpacity.value)
            )
            Text(
                text = "The Bigfoot family gathers near the mine entrance.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(messageOpacity.value)
            )
            Text(
                text = "Something warm and heavy settles around your shoulders.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(finalMessageOpacity.value)
            )
            Text(
                text = "The world slips away again.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(finalMessageOpacity.value)
            )
        }
    }
}