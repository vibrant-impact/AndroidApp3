package com.vi.androidapp3.ui.locations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.vi.androidapp3.audio.AmbientSound
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.InventoryItem
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.Photo
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.ui.components.FakeCameraView
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class SulphurMountainZoomOverlay {
    DOOR_NEEDS_MELT, DOOR_NEEDS_MELT_WITH_MATCHES
}

/**
 * Scene screen for Sulphur Mountain Summit. Features sweeping valley viewpoints,
 * wildlife observations, panoramic photography, and a frozen weather station door puzzle
 * that is cleared using Wooden Matches.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SulphurMountainView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }
    val scope = rememberCoroutineScope()

    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingAlert by remember { mutableStateOf(false) }

    var activePhoto by remember { mutableStateOf<Photo?>(null) }
    var activeZoomOverlay by remember { mutableStateOf<SulphurMountainZoomOverlay?>(null) }

    val hotspots = remember {
        listOf(
            SceneHotspot("observatory_door", "Observatory Door", Rect(859f, 752f, 859f + 223f, 752f + 278f)),
            SceneHotspot("sansons_sign", "Sanson's Peak Sign", Rect(978f, 2056f, 978f + 212f, 2056f + 205f)),
            SceneHotspot("banff_town_view", "View of Banff Below", Rect(284f, 830f, 284f + 466f, 830f + 244f)),
            SceneHotspot("bighorn_sheep", "Bighorn Sheep", Rect(536f, 1231f, 536f + 148f, 1231f + 142f)),
            SceneHotspot("gondola_station", "Gondola Station", Rect(153f, 1441f, 153f + 513f, 1441f + 486f))
        )
    }

    // Manage snowy exterior ambient sound lifecycle
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.SNOWY_EXTERIOR, 1.0f)
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundManager.shared.play(GameSound.LOCATION_TRAVEL, 0.45f)
            SoundManager.shared.stopAmbience(AmbientSound.SNOWY_EXTERIOR)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "sulphur_mountain_base",
            canvasSize = canvasSize,
            hotspots = hotspots,
            overlayObjects = emptyList(),
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                when (hotspot.id) {
                    "observatory_door" -> {
                        SoundManager.shared.play(GameSound.TAP, 0.35f)
                        if (viewModel.hasMeltedWeatherStationDoorIce) {
                            viewModel.currentLocation = LocationId.OBSERVATORY
                        } else if (viewModel.hasInventoryItem(InventoryItem.WOODEN_MATCHES)) {
                            activeZoomOverlay = SulphurMountainZoomOverlay.DOOR_NEEDS_MELT_WITH_MATCHES
                        } else {
                            activeZoomOverlay = SulphurMountainZoomOverlay.DOOR_NEEDS_MELT
                        }
                    }
                    "sansons_sign" -> {
                        alertTitle = "Sanson's Peak Sign"
                        alertMessage = "The sign names this peak after Norman Sanson, the museum's first curator. He climbed this mountain over 1,000 times to record weather data."
                        showingAlert = true
                    }
                    "banff_town_view" -> activePhoto = Photo.sulphurMountain
                    "bighorn_sheep" -> {
                        alertTitle = "Bighorn Sheep"
                        alertMessage = "A bighorn sheep pauses on a precarious ledge, a perfect picture of mountain resilience."
                        showingAlert = true
                    }
                    "gondola_station" -> {
                        alertTitle = "Gondola Station"
                        alertMessage = "The gondola station offers a warm retreat from the summit winds. It connects the base to this grand viewpoint daily."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Sulphur Mountain",
            locationSubtitle = "A view from above the pattern",
            onBagTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingInventory = true },
            onJournalTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingJournal = true }
        )

        Button(
            onClick = {
                SoundManager.shared.play(GameSound.LOCATION_TRAVEL, 0.5f)
                viewModel.currentLocation = LocationId.MUSEUM_INTERIOR },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Return to Museum")
        }

        when (activeZoomOverlay) {
            SulphurMountainZoomOverlay.DOOR_NEEDS_MELT -> {
                HotspotZoomOverlay(
                    title = "Frozen Observatory Door",
                    imageName = "zoom_sulphur_mountain_observatory_door",
                    description = "The observatory door is frozen shut. You need something hot enough to melt the seal.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        activeZoomOverlay = null },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            SulphurMountainZoomOverlay.DOOR_NEEDS_MELT_WITH_MATCHES -> {
                HotspotZoomOverlay(
                    title = "Frozen Observatory Door",
                    imageName = "zoom_sulphur_mountain_observatory_door",
                    description = "The wooden matches might be just enough heat to melt the ice. The wind howls, but the flame holds steady.",
                    primaryButtonTitle = "Use Matches",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.useInventoryItem(InventoryItem.WOODEN_MATCHES)
                        viewModel.hasMeltedWeatherStationDoorIce = true
                        activeZoomOverlay = null
                        scope.launch {
                            delay(250)
                            viewModel.currentLocation = LocationId.OBSERVATORY
                        }
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            null -> {}
        }
    }

    if (showingInventory) {
        ModalBottomSheet(onDismissRequest = { showingInventory = false }) {
            InventoryView(items = viewModel.inventory, onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                showingInventory = false })
        }
    }

    if (showingJournal) {
        ModalBottomSheet(onDismissRequest = { showingJournal = false }) {
            JournalView(
                journalPhotos = viewModel.journalPhotos,
                photoCount = viewModel.photoCount,
                totalPhotoCount = viewModel.totalPhotoCount,
                photoRewardCode = viewModel.photoRewardCode,
                onDismiss = {
                    SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                    showingJournal = false }
            )
        }
    }

    activePhoto?.let { photo ->
        FakeCameraView(
            photo = photo,
            alreadyCaptured = viewModel.hasPhoto(photo),
            onCapture = { captured -> viewModel.capturePhoto(captured)
                SoundManager.shared.play(GameSound.CAMERA_FLASH, 0.45f)
            },
            onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                activePhoto = null }
        )
    }

    if (showingAlert) {
        AlertDialog(
            onDismissRequest = { showingAlert = false },
            title = { Text(alertTitle) },
            text = { Text(alertMessage) },
            confirmButton = { TextButton(onClick = { showingAlert = false }) { Text("OK") } }
        )
    }
}