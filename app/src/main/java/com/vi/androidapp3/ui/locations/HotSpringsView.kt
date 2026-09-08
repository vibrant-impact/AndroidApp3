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

private enum class HotSpringsZoomOverlay {
    MAP_TABLE, KEYS_AVAILABLE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotSpringsView(
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
    var activeZoomOverlay by remember { mutableStateOf<HotSpringsZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("marilyn_monroe_picture", "Marilyn Monroe Picture", Rect(839f, 1416f, 839f + 382f, 1416f + 538f)),
            SceneHotspot("keys", "Keys on Board", Rect(1079f, 2033f, 1079f + 186f, 2033f + 199f)),
            SceneHotspot("lodge", "Hot Springs Lodge", Rect(664f, 840f, 664f + 626f, 840f + 424f)),
            SceneHotspot("map_table", "Table with Map", Rect(1010f, 2302f, 1010f + 280f, 2302f + 233f)),
            SceneHotspot("shed", "Shed", Rect(0f, 1585f, 211f, 1585f + 317f)),
            SceneHotspot("hot_pool", "Hot Spring Pool", Rect(263f, 1262f, 263f + 583f, 1262f + 292f))
        )
    }

    val activeHotspots = remember(viewModel.hasTradedVintageBrassToken, viewModel.hasCollectedObservatoryLockerKey, viewModel.hasFoundCafeLead) {
        rawHotspots.filter {
            when (it.id) {
                "keys" -> viewModel.hasTradedVintageBrassToken && !viewModel.hasCollectedObservatoryLockerKey
                "map_table" -> !viewModel.hasFoundCafeLead
                else -> true
            }
        }
    }

    val activeOverlayObjects = remember(viewModel.hasCollectedObservatoryLockerKey, viewModel.hasFoundCafeLead) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasCollectedObservatoryLockerKey) {
            list.add(SceneOverlayObject("key_gone", "hot_springs_key_gone_overlay", Rect(1119f, 2062f, 1119f + 76f, 2062f + 157f)))
        }
        if (viewModel.hasFoundCafeLead) {
            list.add(SceneOverlayObject("cafe_lead_gone", "hot_springs_cafe_lead_gone_overlay", Rect(970f, 2356f, 970f + 218f, 2356f + 162f)))
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
            imageName = "hot_springs_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "marilyn_monroe_picture" -> activePhoto = Photo.hotSprings
                    "keys" -> activeZoomOverlay = HotSpringsZoomOverlay.KEYS_AVAILABLE
                    "map_table" -> activeZoomOverlay = HotSpringsZoomOverlay.MAP_TABLE
                    "lodge" -> {
                        alertTitle = "Hot Springs Lodge"
                        alertMessage = "The lodge is quiet, but the windows glow with mountain warmth."
                        showingAlert = true
                    }
                    "shed" -> {
                        alertTitle = "Small Shed"
                        alertMessage = "A small shed beside the hot springs—practical as ever, built for chores and sore hands."
                        showingAlert = true
                    }
                    "hot_pool" -> {
                        alertTitle = "Hot Spring Pool"
                        alertMessage = "Steam rolls over the mineral water. Even in deep winter, the pool refuses to freeze."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Upper Hot Springs",
            locationSubtitle = "Warm refuge in a frozen world",
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
            HotSpringsZoomOverlay.MAP_TABLE -> {
                HotspotZoomOverlay(
                    title = "Map on the Table",
                    imageName = "zoom_hot_springs_cafe_lead",
                    description = "A worn map and research notes lie on the table. The researcher could be helpful to chat with.\n\nScribblings on a napkin point toward the 'Snowy Owl Cafe' in downtown Banff.",
                    primaryButtonTitle = "Take Cafe Lead",
                    onPrimaryAction = {
                        if (!viewModel.hasFoundCafeLead) {
                            SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                            viewModel.collectInventoryItem(InventoryItem.CAFE_LEAD)
                        }
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.CAFE_LEAD
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null }
                )
            }
            HotSpringsZoomOverlay.KEYS_AVAILABLE -> {
                HotspotZoomOverlay(
                    title = "Keys on Board",
                    imageName = "zoom_hot_springs_keys",
                    description = "There it is, the green key that the researcher promised. It's onward to the Sulphur Mountain Observatory from here.",
                    primaryButtonTitle = "Take Observatory Key",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.collectInventoryItem(InventoryItem.OBSERVATORY_LOCKER_KEY)
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.OBSERVATORY_LOCKER_KEY
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
                SoundManager.shared.play(GameSound.CAMERA_FLASH, 0.45f)},
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