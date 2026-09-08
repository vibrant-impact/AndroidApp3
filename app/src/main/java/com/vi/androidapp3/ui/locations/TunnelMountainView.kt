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
import com.vi.androidapp3.data.SceneOverlayObject
import com.vi.androidapp3.ui.components.FakeCameraView
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.IcicleFallSequenceView
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class TunnelMountainZoomOverlay {
    CAVE_ENTRANCE_NEEDS_AXE, CAVE_ENTRANCE_WITH_AXE
}

/**
 * Scene screen for Tunnel Mountain. Features snowy owl wildlife photography,
 * trail tracks, and the boarded cave entrance puzzle requiring the Woodcutter's Axe
 * to trigger the falling icicle sequence into Bigfoot's Lair.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunnelMountainView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }
    val scope = rememberCoroutineScope()

    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }
    var showingIcicleFallSequence by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingAlert by remember { mutableStateOf(false) }

    var activePhoto by remember { mutableStateOf<Photo?>(null) }
    var activeZoomOverlay by remember { mutableStateOf<TunnelMountainZoomOverlay?>(null) }

    val hotspots = remember {
        listOf(
            SceneHotspot("cave_entrance", "Boarded Cave Entrance", Rect(802f, 780f, 802f + 447f, 780f + 690f)),
            SceneHotspot("bigfoot_footprint", "Bigfoot Footprint", Rect(896f, 2152f, 896f + 304f, 2152f + 251f)),
            SceneHotspot("bench", "Bench", Rect(740f, 1786f, 740f + 398f, 1786f + 344f)),
            SceneHotspot("snowy_owl", "Snowy Owl", Rect(59f, 650f, 59f + 145f, 650f + 234f)),
            SceneHotspot("fox", "Fox", Rect(153f, 1931f, 153f + 164f, 1931f + 192f))
        )
    }

    val activeOverlayObjects = remember(viewModel.hasReturnedFromBigfootLair, viewModel.hasEscapedBigfootLair) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasReturnedFromBigfootLair || viewModel.hasEscapedBigfootLair) {
            list.add(SceneOverlayObject("sealed_cave", "tunnel_mountain_sealed_cave_overlay", Rect(802f, 780f, 802f + 447f, 780f + 690f)))
        }
        list
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
            imageName = "tunnel_mountain_base",
            canvasSize = canvasSize,
            hotspots = hotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "cave_entrance" -> {
                        SoundManager.shared.play(GameSound.TAP, 0.35f)
                        if (viewModel.hasReturnedFromBigfootLair || viewModel.hasEscapedBigfootLair) {
                            alertTitle = "Where Did It Go?"
                            alertMessage = "I swear there was a cave entrance here.\n\nThe snow has shifted, the rocks look different, and my head is still fuzzy from the fall.\n\nMaybe that is for the best."
                            showingAlert = true
                        } else if (viewModel.hasBrokenCaveEntranceBoards) {
                            alertTitle = "Unstable Ice"
                            alertMessage = "The broken entrance is too dangerous to approach again."
                            showingAlert = true
                        } else {
                            activeZoomOverlay = if (viewModel.hasInventoryItem(InventoryItem.WOODCUTTERS_AXE)) {
                                TunnelMountainZoomOverlay.CAVE_ENTRANCE_WITH_AXE
                            } else {
                                TunnelMountainZoomOverlay.CAVE_ENTRANCE_NEEDS_AXE
                            }
                        }
                    }
                    "bigfoot_footprint" -> {
                        alertTitle = "Another Huge Footprint"
                        alertMessage = "This unusually large print is like the one I saw by the museum. I'm not sure I want to meet the owner, but I sure am curious!"
                        showingAlert = true
                    }
                    "bench" -> {
                        alertTitle = "Trail Bench"
                        alertMessage = "The bench offers a perfect view of the trail, the trees, and the realization that something much larger than you recently walked by."
                        showingAlert = true
                    }
                    "snowy_owl" -> activePhoto = Photo.tunnelMountain
                    "fox" -> {
                        alertTitle = "Fox"
                        alertMessage = "The fox pauses just long enough to look suspicious, then trots away with the confidence of someone who knows these trails."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Tunnel Mountain",
            locationSubtitle = "Tracks between town and timber",
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
            TunnelMountainZoomOverlay.CAVE_ENTRANCE_NEEDS_AXE -> {
                HotspotZoomOverlay(
                    title = "Boarded Cave Entrance",
                    imageName = "zoom_tunnel_mountain_cave_entrance",
                    description = "Old boards block the cave entrance. They are frozen, warped, and too thick to break by hand.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.85f)
                        activeZoomOverlay = null },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            TunnelMountainZoomOverlay.CAVE_ENTRANCE_WITH_AXE -> {
                HotspotZoomOverlay(
                    title = "Boarded Cave Entrance",
                    imageName = "zoom_tunnel_mountain_cave_entrance",
                    description = "I should probably take heed and 'KEEP OUT,' but my curiosity is burning!\n\nI could probably hack through these boards with an axe. Anything for my next big story, right?!",
                    primaryButtonTitle = "Use Axe",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.CHOP_WOOD, 0.9f)
                        viewModel.useInventoryItem(InventoryItem.WOODCUTTERS_AXE)
                        viewModel.hasBrokenCaveEntranceBoards = true
                        viewModel.hasTriggeredIcicleFall = true
                        activeZoomOverlay = null

                        scope.launch {
                            delay(200)
                            showingIcicleFallSequence = true
                        }
                    },
                    onClose = { activeZoomOverlay = null }
                )
            }
            null -> {}
        }

        if (showingIcicleFallSequence) {
            IcicleFallSequenceView(
                onFinished = {
                    showingIcicleFallSequence = false
                    viewModel.hasWokenInBigfootLair = true
                    viewModel.currentLocation = LocationId.BIGFOOT_LAIR
                }
            )
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