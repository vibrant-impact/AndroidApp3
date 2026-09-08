package com.vi.androidapp3.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Animated hazard sequence in the cave where a giant icicle splits and falls,
 * triggering screen shake, impact flashes, and a narrative blackout.
 */
@Composable
fun IcicleFallSequenceView(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val bgResId = remember {
        context.resources.getIdentifier("tunnel_mountain_cave_break_sequence", "drawable", context.packageName)
    }
    val icicleResId = remember {
        context.resources.getIdentifier("falling_icicle", "drawable", context.packageName)
    }

    // Animation states for the falling icicle
    val icicleY = remember { Animatable(-420f) }
    val icicleX = remember { Animatable(0f) }
    val icicleRotation = remember { Animatable(-8f) }
    val icicleOpacity = remember { Animatable(0f) }

    // Screen shockwave states
    val sceneScale = remember { Animatable(1f) }
    val sceneShakeX = remember { Animatable(0f) }
    val sceneShakeY = remember { Animatable(0f) }

    // Flash, blackout, and narrative text opacities
    val impactFlashOpacity = remember { Animatable(0f) }
    val blackoutOpacity = remember { Animatable(0f) }
    val textOpacity = remember { Animatable(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val screenHeight = maxHeight.value

        // Timed choreography for the falling hazard sequence
        LaunchedEffect(Unit) {
            delay(1150)
            icicleOpacity.animateTo(1f, tween(250))
            SoundManager.shared.play(GameSound.CHOP_WOOD, 0.9f)

            delay(350)
            icicleY.animateTo(screenHeight * 0.5f, tween(550))
            icicleX.snapTo(-18f)
            icicleRotation.snapTo(18f)
            SoundManager.shared.play(GameSound.ICE_CRACK, 1.0f)

            impactFlashOpacity.snapTo(1f)
            sceneScale.snapTo(1.06f)

            // Screen shake offsets simulation
            val shakes = listOf(
                Pair(-22f, 8f), Pair(24f, -7f), Pair(-18f, 6f),
                Pair(16f, -5f), Pair(-10f, 3f), Pair(7f, -2f), Pair(0f, 0f)
            )
            shakes.forEach { (x, y) ->
                sceneShakeX.snapTo(x)
                sceneShakeY.snapTo(y)
                delay(55)
            }
            sceneScale.animateTo(1f, tween(250))
            impactFlashOpacity.animateTo(0f, tween(380))
            SoundManager.shared.play(GameSound.ICICLE_CRASH, 1.0f)

            delay(100)
            blackoutOpacity.animateTo(1f, tween(750))
            SoundManager.shared.play(GameSound.BLACKOUT_RUMBLE, 1.0f)

            delay(400)
            textOpacity.animateTo(1f, tween(700))

            delay(4000)
            onFinished()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(sceneScale.value)
                .offset { IntOffset(sceneShakeX.value.roundToInt(), sceneShakeY.value.roundToInt()) }
        ) {
            if (bgResId != 0) {
                Image(
                    painter = painterResource(id = bgResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (icicleResId != 0) {
                Image(
                    painter = painterResource(id = icicleResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                        .align(Alignment.TopCenter)
                        .offset { IntOffset(icicleX.value.roundToInt(), icicleY.value.roundToInt()) }
                        .rotate(icicleRotation.value)
                        .alpha(icicleOpacity.value)
                        .size(110.dp)
                )
            }
        }

        if (impactFlashOpacity.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(impactFlashOpacity.value)
                    .background(Color.White)
            )
        }

        if (blackoutOpacity.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(blackoutOpacity.value)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .alpha(textOpacity.value)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("The boards split with a sharp crack.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Something above you gives way.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Then everything goes black.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}