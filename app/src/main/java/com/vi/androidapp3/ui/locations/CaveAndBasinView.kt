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
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

private enum class CaveAndBasinZoomOverlay {
    SUBMERGED_CHEST_NEEDS_HOOK, SUBMERGED_CHEST_WITH_HOOK
}

/**
 * Scene screen for the Cave and Basin historic site.
 * Features cave drip ambient audio, wildlife hotspots, historic plaques,
 * and a submerged chest puzzle requiring the Gaff Hook.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaveAndBasinView(
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
    var activeZoomOverlay by remember { mutableStateOf<CaveAndBasinZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("submerged_chest", "Submerged Chest", Rect(616f, 1704f, 616f + 204f, 1704f + 185f)),
            SceneHotspot("vent", "Cave Vent Hole", Rect(345f, 262f, 345f + 588f, 262f + 675f)),
            SceneHotspot("squirrel", "Squirrel in Cozy Nook", Rect(1033f, 1009f, 1033f + 221f, 1009f + 335f)),
            SceneHotspot("bats", "Bats", Rect(176f, 717f, 176f + 150f, 717f + 298f)),
            SceneHotspot("plaque", "Plaque", Rect(55f, 1981f, 55f + 382f, 1981f + 264f))
        )
    }

    val activeHotspots = remember(viewModel.hasCollectedVintageBrassToken) {
        rawHotspots.filter { if (it.id == "submerged_chest") !viewModel.hasCollectedVintageBrassToken else true }
    }

    val activeOverlayObjects = remember(viewModel.hasOpenedBasinChest) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasOpenedBasinChest) {
            list.add(SceneOverlayObject("chest_open", "basin_chest_open_overlay", Rect(587f, 1598f, 587f + 313f, 1598f + 315f)))
        }
        list
    }

    // Manage cave drip ambient sound lifecycle
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
        ImageSceneView(
            imageName = "cave_and_basin_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "submerged_chest" -> {
                        activeZoomOverlay = if (viewModel.hasInventoryItem(InventoryItem.GAFF_HOOK)) {
                            CaveAndBasinZoomOverlay.SUBMERGED_CHEST_WITH_HOOK
                        } else {
                            CaveAndBasinZoomOverlay.SUBMERGED_CHEST_NEEDS_HOOK
                        }
                    }
                    "vent" -> activePhoto = Photo.caveAndBasin
                    "squirrel" -> {
                        alertTitle = "Cozy Squirrel"
                        alertMessage = "A squirrel has found the warmest nook in the cave and seems deeply unwilling to give up the lease."
                        showingAlert = true
                    }
                    "bats" -> {
                        alertTitle = "Bats"
                        alertMessage = "The bats hang quietly in the mineral-scented dark, ignoring both tourism and legend."
                        showingAlert = true
                    }
                    "plaque" -> {
                        alertTitle = "Historic Plaque"
                        alertMessage = "The plaque explains the official beginning of Canada's national park system. The cave itself feels much older than any plaque."
                        showingAlert = true
                    }
                }
            }
        )

        TopHUDView(
            locationTitle = "Cave and Basin",
            locationSubtitle = "Steam, stone, and old beginnings",
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
            CaveAndBasinZoomOverlay.SUBMERGED_CHEST_NEEDS_HOOK -> {
                HotspotZoomOverlay(
                    title = "Submerged Chest",
                    imageName = "zoom_basin_chest",
                    description = "A small chest rests below the surface of the steaming pool. It is too far down to reach by hand.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        activeZoomOverlay = null },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            CaveAndBasinZoomOverlay.SUBMERGED_CHEST_WITH_HOOK -> {
                HotspotZoomOverlay(
                    title = "Submerged Chest",
                    imageName = "zoom_basin_chest",
                    description = "The gaff hook pries the chest open. You notice a brassy vintage token inside.",
                    primaryButtonTitle = "Take Token",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasOpenedBasinChest = true
                        viewModel.useInventoryItem(InventoryItem.GAFF_HOOK)
                        viewModel.collectInventoryItem(InventoryItem.VINTAGE_BRASS_TOKEN)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.VINTAGE_BRASS_TOKEN
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