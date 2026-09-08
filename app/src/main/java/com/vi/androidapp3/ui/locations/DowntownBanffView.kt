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
import com.vi.androidapp3.ui.components.FakeCameraView
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.ItemCollectedOverlay
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

private enum class DowntownZoomOverlay {
    CAFE_NO_LEAD, CAFE_NO_TOKEN, CAFE_TRADE_TOKEN, CAFE_AFTER_TRADE
}

/**
 * Scene screen for Downtown Banff. Features town street ambient audio,
 * iconic sculpture photography objectives, and the Snowy Owl Cafe trade puzzle
 * where the Vintage Brass Token is exchanged for the observatory lead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DowntownBanffView(
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
    var activeZoomOverlay by remember { mutableStateOf<DowntownZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val hotspots = remember {
        listOf(
            SceneHotspot("cafe", "Snowy Owl Cafe", Rect(0f, 1437f, 341f, 1437f + 465f)),
            SceneHotspot("bigfoot_ice_sculpture", "Bigfoot Ice Sculpture", Rect(697f, 1504f, 697f + 443f, 1504f + 488f)),
            SceneHotspot("horse_buggy", "Horse Buggy", Rect(215f, 1970f, 215f + 330f, 1970f + 376f)),
            SceneHotspot("town_hall", "Town Hall", Rect(935f, 856f, 935f + 355f, 856f + 662f)),
            SceneHotspot("cascade_mountain", "Cascade Mountain", Rect(419f, 391f, 419f + 605f, 391f + 725f))
        )
    }

    // Manage town street ambient sound lifecycle
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.TOWN_STREET, 0.85f)
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundManager.shared.stopAmbience(AmbientSound.TOWN_STREET)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "downtown_base",
            canvasSize = canvasSize,
            hotspots = hotspots,
            overlayObjects = emptyList(),
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "cafe" -> {
                        activeZoomOverlay = when {
                            !viewModel.hasFoundCafeLead -> DowntownZoomOverlay.CAFE_NO_LEAD
                            !viewModel.hasInventoryItem(InventoryItem.VINTAGE_BRASS_TOKEN) -> DowntownZoomOverlay.CAFE_NO_TOKEN
                            !viewModel.hasTradedVintageBrassToken -> DowntownZoomOverlay.CAFE_TRADE_TOKEN
                            else -> DowntownZoomOverlay.CAFE_AFTER_TRADE
                        }
                    }
                    "bigfoot_ice_sculpture" -> activePhoto = Photo.downtownBanff
                    "horse_buggy" -> {
                        alertTitle = "Horse Buggy"
                        alertMessage = "The buggy looks decorative now, but Banff winters were once crossed one frozen street at a time."
                        showingAlert = true
                    }
                    "town_hall" -> {
                        alertTitle = "Town Hall"
                        alertMessage = "The town hall stands steady under the mountain shadows. Official records rarely mention legends unless they cause paperwork."
                        showingAlert = true
                    }
                    "cascade_mountain" -> {
                        alertTitle = "Cascade Mountain"
                        alertMessage = "Cascade Mountain rises over town like a frozen wall. Somewhere beyond the familiar streets, the wilderness begins making its own rules."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Downtown Banff",
            locationSubtitle = "A warm cafe and a local researcher",
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
            DowntownZoomOverlay.CAFE_NO_LEAD -> {
                HotspotZoomOverlay(
                    title = "Snowy Owl Cafe",
                    imageName = "zoom_downtown_cafe_interior",
                    description = "The cafe is warm and busy. A researcher looks preoccupied, perhaps waiting for someone.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        activeZoomOverlay = null },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            DowntownZoomOverlay.CAFE_NO_TOKEN -> {
                HotspotZoomOverlay(
                    title = "Snowy Owl Cafe",
                    imageName = "zoom_downtown_cafe_interior",
                    description = "The researcher looks up as you approach. 'Another token?' they sigh, 'Unless it's connected to the old Banff Spring records, I'm afraid I haven't the time.'",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        activeZoomOverlay = null },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            DowntownZoomOverlay.CAFE_TRADE_TOKEN -> {
                HotspotZoomOverlay(
                    title = "Snowy Owl Cafe",
                    imageName = "zoom_downtown_cafe_interior",
                    description = "The researcher's eyes light up at the sight of the vintage brass token.\n\n'In exchange for this token, I can help you. You should investigate the Sulphur Mountain Observatory. Take the observatory locker key that I left at the hot springs. It's the only green key on the board.'",
                    primaryButtonTitle = "Trade Token",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasTradedVintageBrassToken = true
                        viewModel.useInventoryItem(InventoryItem.VINTAGE_BRASS_TOKEN)
                        viewModel.collectInventoryItem(InventoryItem.OBSERVATORY_STORY_LEAD)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.OBSERVATORY_STORY_LEAD
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            DowntownZoomOverlay.CAFE_AFTER_TRADE -> {
                HotspotZoomOverlay(
                    title = "Snowy Owl Cafe",
                    imageName = "zoom_downtown_cafe_interior",
                    description = "The researcher is now absorbed in studying the token, muttering about early park archives. The lead to the observatory key is safe in your journal.",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        activeZoomOverlay = null },
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