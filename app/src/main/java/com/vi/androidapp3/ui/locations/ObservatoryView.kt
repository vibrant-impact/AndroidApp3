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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.InventoryItem
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.data.SceneOverlayObject
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.ItemCollectedOverlay
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

private enum class ObservatoryZoomOverlay {
    LOCKER_WITH_KEY, LOGBOOK
}

/**
 * Scene screen for the Sulphur Mountain Observatory weather station.
 * Features old equipment inspection, logbook reading yielding tunnel mountain lore,
 * and a cabinet lock puzzle unlocked using the Observatory Locker Key.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservatoryView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }

    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingAlert by remember { mutableStateOf(false) }

    var activeZoomOverlay by remember { mutableStateOf<ObservatoryZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("locker", "Observatory Locker", Rect(748f, 1410f, 748f + 207f, 1410f + 289f)),
            SceneHotspot("logbook", "Observatory Logbook", Rect(955f, 1214f, 955f + 335f, 1214f + 566f)),
            SceneHotspot("old_equipment", "Old Observatory Equipment", Rect(20f, 1217f, 20f + 553f, 1217f + 512f))
        )
    }

    val activeHotspots = remember(viewModel.hasInventoryItem(InventoryItem.OBSERVATORY_LOCKER_KEY), viewModel.hasCollectedRustyCrowbar) {
        rawHotspots.filter {
            if (it.id == "locker") {
                viewModel.hasInventoryItem(InventoryItem.OBSERVATORY_LOCKER_KEY) && !viewModel.hasCollectedRustyCrowbar
            } else true
        }
    }

    val activeOverlayObjects = remember(viewModel.hasCollectedRustyCrowbar) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasCollectedRustyCrowbar) {
            list.add(SceneOverlayObject("locker_open", "observatory_lock_gone_overlay", Rect(744f, 1466f, 744f + 204f, 1466f + 238f)))
        }
        list
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "observatory_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "locker" -> {
                        if (viewModel.hasInventoryItem(InventoryItem.OBSERVATORY_LOCKER_KEY)) {
                            activeZoomOverlay = ObservatoryZoomOverlay.LOCKER_WITH_KEY
                        } else {
                            alertTitle = "Locked Cabinet"
                            alertMessage = "The cabinet is locked tight. You need a key."
                            showingAlert = true
                        }
                    }
                    "logbook" -> activeZoomOverlay = ObservatoryZoomOverlay.LOGBOOK
                    "old_equipment" -> {
                        alertTitle = "Old Observatory Equipment"
                        alertMessage = "The equipment is dusty and dated. Whoever worked here believed logs and measurements mattered — even when no one else was watching."
                        showingAlert = true
                    }
                }
            }
        )

        TopHUDView(
            locationTitle = "Sulphur Mountain Observatory",
            locationSubtitle = "Records from the summit",
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
                viewModel.currentLocation = LocationId.SULPHUR_MOUNTAIN },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Back Outside")
        }

        when (activeZoomOverlay) {
            ObservatoryZoomOverlay.LOCKER_WITH_KEY -> {
                HotspotZoomOverlay(
                    title = "Observatory Locker",
                    imageName = "zoom_observatory_lock",
                    description = "The locker clicks open with the key. Inside, nestled in an oily cloth, is a rusty crowbar. It looks heavy enough to pry through thick ice or old boards.",
                    primaryButtonTitle = "Take Crowbar",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.useInventoryItem(InventoryItem.OBSERVATORY_LOCKER_KEY)
                        viewModel.collectInventoryItem(InventoryItem.RUSTY_CROWBAR)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.RUSTY_CROWBAR
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            ObservatoryZoomOverlay.LOGBOOK -> {
                HotspotZoomOverlay(
                    title = "Sanson's Logbook",
                    imageName = "zoom_observatory_logbook",
                    description = "The logbook is full of weather notes, summit conditions, and tiny sketches in the margins.\n\nOne entry is circled:\n\n“Large tracks visible below Tunnel Mountain after last snowfall. Not bear. Not boot. Direction suggests possible cave access above the timberline.”\n\nI wonder what's in that cave.",
                    primaryButtonTitle = "Add to Journal",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasReadObservatoryLogbook = true
                        viewModel.collectInventoryItem(InventoryItem.OBSERVATORY_JOURNAL_LEAD)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.OBSERVATORY_STORY_LEAD
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

    if (showingAlert) {
        AlertDialog(
            onDismissRequest = { showingAlert = false },
            title = { Text(alertTitle) },
            text = { Text(alertMessage) },
            confirmButton = { TextButton(onClick = { showingAlert = false }) { Text("OK") } }
        )
    }
}