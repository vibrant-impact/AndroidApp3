package com.vi.androidapp3.ui.locations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.AmbientSound
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.ui.components.BigfootEvidenceCameraView
import com.vi.androidapp3.ui.components.BigfootLairBlackoutView
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class BigfootLairOverlay {
    CAVE_EXIT, BIGFOOT_FAMILY, LOST_LEMON_MINE
}

/**
 * Secret endgame scene inside Bigfoot's Lair.
 * Handles the wake-up blur sequence, cave explorations, Bigfoot photo evidence capture,
 * and triggers the final escape sequence.
 */
@Composable
fun BigfootLairView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }
    val scope = rememberCoroutineScope()

    var activeOverlay by remember { mutableStateOf<BigfootLairOverlay?>(null) }
    var showingBigfootCamera by remember { mutableStateOf(false) }
    var showingFinalBlackout by remember { mutableStateOf(false) }

    // Wake-up sequence visual transition states
    val wakeUpBlur = remember { Animatable(18f) }
    val wakeUpBlackOpacity = remember { Animatable(1f) }
    val wakeUpTextOpacity = remember { Animatable(0f) }
    var isShowingWakeUpSequence by remember { mutableStateOf(true) }

    val hotspots = remember {
        listOf(
            SceneHotspot("cave_exit", "Cave Entrance Above", Rect(338f, 222f, 338f + 759f, 222f + 661f)),
            SceneHotspot("bigfoot_family", "Bigfoot Family", Rect(145f, 1725f, 145f + 662f, 1725f + 721f)),
            SceneHotspot("lost_lemon_mine", "Lost Lemon Mine", Rect(13f, 981f, 13f + 515f, 981f + 665f)),
            SceneHotspot("bigfoot", "Bigfoot", Rect(787f, 1292f, 787f + 374f, 1292f + 666f))
        )
    }

    // Execute wake-up fade animation on screen entry
    LaunchedEffect(Unit) {
        delay(350)
        wakeUpTextOpacity.animateTo(1f, tween(900))
        delay(350)
        wakeUpBlackOpacity.animateTo(0.35f, tween(2200))
        wakeUpBlur.animateTo(8f, tween(2200))
        delay(100)
        wakeUpBlackOpacity.animateTo(0f, tween(1800))
        wakeUpBlur.animateTo(0f, tween(1800))
        wakeUpTextOpacity.animateTo(0f, tween(1800))
        delay(200)
        isShowingWakeUpSequence = false
    }

    // Triggers final blackout sequence once all key cave interactions are completed
    fun checkForLairCompletion() {
        if (viewModel.hasCompletedRequiredLairInteractions) {
            scope.launch {
                delay(800)
                showingFinalBlackout = true
            }
        }
    }

    // Manage cave drip ambient audio lifecycle
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.CAVE_DRIP, 0.85f)
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundManager.shared.stopAmbience(AmbientSound.CAVE_DRIP)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(wakeUpBlur.value.dp)
        ) {
            ImageSceneView(
                imageName = "bigfoot_lair_base",
                canvasSize = canvasSize,
                hotspots = hotspots,
                overlayObjects = emptyList(),
                showDebugHotspots = false,
                onHotspotTapped = { hotspot ->
                    SoundManager.shared.play(GameSound.TAP, 0.4f)
                    when (hotspot.id) {
                        "cave_exit" -> {
                            viewModel.hasInspectedLairExit = true
                            activeOverlay = BigfootLairOverlay.CAVE_EXIT
                        }
                        "bigfoot_family" -> {
                            viewModel.hasMetBigfootFamily = true
                            activeOverlay = BigfootLairOverlay.BIGFOOT_FAMILY
                        }
                        "lost_lemon_mine" -> {
                            viewModel.hasInspectedLostLemonMine = true
                            activeOverlay = BigfootLairOverlay.LOST_LEMON_MINE
                        }
                        "bigfoot" -> showingBigfootCamera = true
                    }
                }
            )

            // Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Unknown Cave", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("You are hurt, disoriented, and unsure of your location.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.75f))
                }
            }

            when (activeOverlay) {
                BigfootLairOverlay.CAVE_EXIT -> {
                    HotspotZoomOverlay(
                        title = "The Way Out",
                        imageName = "zoom_lair_cave_exit",
                        description = "Far above you, cold daylight spills through a jagged opening in the rock.\n\nYou have no idea where you are.\n\nYour head aches. Your shoulder burns. Even if you could reach the wall, you are in no condition to climb out.",
                        primaryButtonTitle = "Close",
                        onPrimaryAction = {
                            SoundManager.shared.play(GameSound.CLOSE, 0.85f)
                            activeOverlay = null
                            checkForLairCompletion()
                        },
                        onClose = {
                            activeOverlay = null
                            checkForLairCompletion()
                        }
                    )
                }
                BigfootLairOverlay.BIGFOOT_FAMILY -> {
                    HotspotZoomOverlay(
                        title = "Gentle Hands",
                        imageName = "zoom_lair_bigfoot_family",
                        description = "A smaller Bigfoot seems eager to help while another carefully arranges old first aid supplies.\n\nBandages. A dented tin. Meltwater in a cup.\n\nThey did not bring you here as a prisoner.\n\nThey brought you here because you were hurt.",
                        primaryButtonTitle = "Close",
                        onPrimaryAction = {
                            SoundManager.shared.play(GameSound.CLOSE, 0.85f)
                            activeOverlay = null
                            checkForLairCompletion()
                        },
                        onClose = {
                            activeOverlay = null
                            checkForLairCompletion()
                        }
                    )
                }
                BigfootLairOverlay.LOST_LEMON_MINE -> {
                    HotspotZoomOverlay(
                        title = "The Lost Lemon Mine",
                        imageName = "zoom_lair_lost_lemon_mine",
                        description = "Beyond the cavern wall, a mine opening glitters with an unmistakable shine.\n\nI can't believe my eyes! Am I dreaming? A faded mark is scratched onto the entrance rocks:\n\nLEMON",
                        primaryButtonTitle = "Close",
                        onPrimaryAction = {
                            SoundManager.shared.play(GameSound.CLOSE, 0.85f)
                            activeOverlay = null
                            checkForLairCompletion()
                        },
                        onClose = {
                            activeOverlay = null
                            checkForLairCompletion()
                        }
                    )
                }
                null -> {}
            }

            if (showingFinalBlackout) {
                BigfootLairBlackoutView(onFinished = { viewModel.finishBigfootLairSequence() })
            }
        }

        // Initial wake-up narrative text layer
        if (isShowingWakeUpSequence) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(wakeUpBlackOpacity.value)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .alpha(wakeUpTextOpacity.value)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Your eyes open slowly.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Stone. Cold air. A distant drip of water.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("You are not on Tunnel Mountain anymore.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }

    if (showingBigfootCamera) {
        BigfootEvidenceCameraView(
            onCapture = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                viewModel.hasTakenBigfootEvidencePhoto = true
                checkForLairCompletion()
            },
            onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                showingBigfootCamera = false }
        )
    }
}