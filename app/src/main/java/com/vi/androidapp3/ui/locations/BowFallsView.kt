package com.vi.androidapp3.ui.locations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
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
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.ItemCollectedOverlay
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

private enum class BowFallsZoomOverlay {
    GAFF_HOOK, BURIED_CANISTER_NEEDS_SHOVEL, BURIED_CANISTER_WITH_SHOVEL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowFallsView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }

    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingAlert by remember { mutableStateOf(false) }

    var activePhoto by remember { mutableStateOf<Photo?>(null) }
    var activeZoomOverlay by remember { mutableStateOf<BowFallsZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("buried_canister", "Buried Canister", Rect(185f, 1862f, 185f + 303f, 1862f + 253f)),
            SceneHotspot("gaff_hook", "Gaff Hook", Rect(935f, 1698f, 935f + 154f, 1698f + 462f)),
            SceneHotspot("douglas_fir_trees", "Douglas Fir Trees", Rect(844f, 0f, 844f + 446f, 1439f)),
            SceneHotspot("frozen_falls", "Frozen Falls", Rect(243f, 830f, 243f + 482f, 830f + 634f))
        )
    }

    val activeHotspots = remember(viewModel.hasCollectedGaffHook, viewModel.hasCollectedWoodenMatches) {
        rawHotspots.filter { hotspot ->
            when (hotspot.id) {
                "gaff_hook" -> !viewModel.hasCollectedGaffHook
                "buried_canister" -> !viewModel.hasCollectedWoodenMatches
                else -> true
            }
        }
    }

    val activeOverlayObjects = remember(viewModel.hasCollectedWoodenMatches, viewModel.hasCollectedGaffHook) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasCollectedWoodenMatches) {
            list.add(SceneOverlayObject("canister_gone", "falls_canister_gone_overlay", Rect(237f, 1894f, 237f + 201f, 1894f + 175f)))
        }
        if (viewModel.hasCollectedGaffHook) {
            list.add(SceneOverlayObject("hook_gone", "falls_gaff_hook_gone_overlay", Rect(769f, 1665f, 769f + 372f, 1665f + 635f)))
        }
        list
    }

    // 1. Ambience Management: Start river/winter ambience on enter, stop on exit
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.SNOWY_EXTERIOR, 0.85f)
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundManager.shared.stopAmbience(AmbientSound.SNOWY_EXTERIOR)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "bow_falls_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "gaff_hook" -> {
                        activeZoomOverlay = BowFallsZoomOverlay.GAFF_HOOK
                    }
                    "buried_canister" -> {
                        activeZoomOverlay = if (viewModel.hasInventoryItem(InventoryItem.SMALL_SHOVEL)) {
                            BowFallsZoomOverlay.BURIED_CANISTER_WITH_SHOVEL
                        } else {
                            BowFallsZoomOverlay.BURIED_CANISTER_NEEDS_SHOVEL
                        }
                    }
                    "douglas_fir_trees" -> activePhoto = Photo.bowFalls
                    "frozen_falls" -> {
                        alertTitle = "Frozen Falls"
                        alertMessage = "The frozen falls hang in mid-roar—Banff’s spray trapped in glassy time. Still, beautiful, and silent."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Bow Falls",
            locationSubtitle = "Winter ice wall",
            onBagTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingInventory = true
                          },
            onJournalTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingJournal = true
            }
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
            BowFallsZoomOverlay.GAFF_HOOK -> {
                HotspotZoomOverlay(
                    title = "Gaff Hook",
                    imageName = "zoom_falls_gaff_hook",
                    description = "A long gaff hook rests against the icy boulder. The metal hook is scratched but sturdy.",
                    primaryButtonTitle = "Take Gaff Hook",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.collectInventoryItem(InventoryItem.GAFF_HOOK)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.GAFF_HOOK
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            BowFallsZoomOverlay.BURIED_CANISTER_NEEDS_SHOVEL -> {
                HotspotZoomOverlay(
                    title = "Packed Snowdrift",
                    imageName = "zoom_falls_canister",
                    description = "Something is buried beneath the crusted snow, but the drift is too hard to clear by hand.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                                      },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            BowFallsZoomOverlay.BURIED_CANISTER_WITH_SHOVEL -> {
                HotspotZoomOverlay(
                    title = "Buried Canister",
                    imageName = "zoom_falls_canister",
                    description = "The small shovel cuts through the packed snow. Beneath the drift, you uncover a sealed canister filled with dry wooden matches.",
                    primaryButtonTitle = "Take Wooden Matches",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasOpenedFallsCanister = true
                        viewModel.useInventoryItem(InventoryItem.SMALL_SHOVEL)
                        viewModel.collectInventoryItem(InventoryItem.WOODEN_MATCHES)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.WOODEN_MATCHES
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            null -> {}
        }

        collectedItemOverlay?.let { item ->
            ItemCollectedOverlay(item = item, onDismiss = { collectedItemOverlay = null })
        }
    }

    if (showingInventory) {
        ModalBottomSheet(onDismissRequest = { showingInventory = false }) {
            InventoryView(items = viewModel.inventory, onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                showingInventory = false
            })
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
                    showingJournal = false
                }
            )
        }
    }

    activePhoto?.let { photo ->
        FakeCameraView(
            photo = photo,
            alreadyCaptured = viewModel.hasPhoto(photo),
            onCapture = { captured -> viewModel.capturePhoto(captured)
                SoundManager.shared.play(GameSound.CAMERA_FLASH, 0.85f)
                        },
            onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.85f)
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