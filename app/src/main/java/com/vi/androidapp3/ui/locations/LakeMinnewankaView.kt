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

private enum class LakeMinnewankaZoomOverlay {
    CRATE_NEEDS_CROWBAR, CRATE_WITH_CROWBAR
}

/**
 * Scene screen for Lake Minnewanka. Features underwater ghost town photography,
 * winter lakeside scenery, and a frozen crate puzzle requiring the Rusty Crowbar
 * to retrieve the Woodcutter's Axe.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LakeMinnewankaView(
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
    var activeZoomOverlay by remember { mutableStateOf<LakeMinnewankaZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("crate", "Frozen Crate", Rect(782f, 2281f, 782f + 198f, 2281f + 148f)),
            SceneHotspot("underwater_town", "Underwater Ghost Town", Rect(125f, 1413f, 125f + 686f, 1413f + 375f)),
            SceneHotspot("cabin", "Cabin", Rect(933f, 1042f, 933f + 351f, 1042f + 267f)),
            SceneHotspot("animals", "Animals by Campfire", Rect(408f, 2227f, 408f + 305f, 2227f + 343f)),
            SceneHotspot("rowboat_dock", "Rowboat and Dock", Rect(688f, 1922f, 688f + 418f, 1922f + 239f))
        )
    }

    val activeHotspots = remember(viewModel.hasCollectedWoodcuttersAxe) {
        rawHotspots.filter { if (it.id == "crate") !viewModel.hasCollectedWoodcuttersAxe else true }
    }

    val activeOverlayObjects = remember(viewModel.hasOpenedMinnewankaCrate) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasOpenedMinnewankaCrate) {
            list.add(SceneOverlayObject("crate_open", "lake_minnewanka_crate_open_overlay", Rect(756f, 2259f, 756f + 249f, 2259f + 180f)))
        }
        list
    }

    // Manage snowy exterior ambient sound lifecycle
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
            imageName = "lake_minnewanka_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "crate" -> {
                        activeZoomOverlay = if (viewModel.hasInventoryItem(InventoryItem.RUSTY_CROWBAR)) {
                            LakeMinnewankaZoomOverlay.CRATE_WITH_CROWBAR
                        } else {
                            LakeMinnewankaZoomOverlay.CRATE_NEEDS_CROWBAR
                        }
                    }
                    "underwater_town" -> activePhoto = Photo.lakeMinnewanka
                    "cabin" -> {
                        alertTitle = "Cabin"
                        alertMessage = "The cabin looks warm from a distance, which is exactly how cabins trick people in winter."
                        showingAlert = true
                    }
                    "animals" -> {
                        alertTitle = "Animals by the Fire"
                        alertMessage = "The animals seem unusually comfortable together. Either this is a peaceful forest, or someone promised snacks."
                        showingAlert = true
                    }
                    "rowboat_dock" -> {
                        alertTitle = "Rowboat and Dock"
                        alertMessage = "The rowboat is frozen in place. Whatever lies under Lake Minnewanka will have to stay underwater a little longer."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Lake Minnewanka",
            locationSubtitle = "A drowned town beneath the ice",
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
            LakeMinnewankaZoomOverlay.CRATE_NEEDS_CROWBAR -> {
                HotspotZoomOverlay(
                    title = "Frozen Crate",
                    imageName = "zoom_lake_minnewanka_crate",
                    description = "The crate is frozen shut. The lid will not budge by hand.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = { activeZoomOverlay = null },
                    onClose = { activeZoomOverlay = null }
                )
            }
            LakeMinnewankaZoomOverlay.CRATE_WITH_CROWBAR -> {
                HotspotZoomOverlay(
                    title = "Frozen Crate",
                    imageName = "zoom_lake_minnewanka_crate",
                    description = "The rusty crowbar bites into the frozen seam. With one hard pull, the crate cracks open. Inside is a woodcutter's axe.",
                    primaryButtonTitle = "Take Axe",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasOpenedMinnewankaCrate = true
                        viewModel.useInventoryItem(InventoryItem.RUSTY_CROWBAR)
                        viewModel.collectInventoryItem(InventoryItem.WOODCUTTERS_AXE)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.WOODCUTTERS_AXE
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
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