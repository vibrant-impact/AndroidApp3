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
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.Photo
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.data.SceneOverlayObject
import com.vi.androidapp3.ui.components.FakeCameraView
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanffSpringsHotelView(
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

    val rawHotspots = remember {
        listOf(
            SceneHotspot("luggage", "Luggage", Rect(84f, 2016f, 84f + 495f, 2016f + 415f)),
            SceneHotspot("ghost_bride", "Ghost Bride", Rect(719f, 1115f, 719f + 157f, 1115f + 132f)),
            SceneHotspot("horse_drawn_carriage", "Horse-Drawn Carriage", Rect(290f, 1731f, 290f + 318f, 1731f + 196f)),
            SceneHotspot("foot_bridge", "Foot Bridge", Rect(755f, 1825f, 755f + 409f, 1825f + 184f)),
            SceneHotspot("main_doors", "Main Doors", Rect(638f, 1568f, 638f + 257f, 1568f + 278f))
        )
    }

    val activeHotspots = remember(viewModel.hasPhotographedGhostBride) {
        rawHotspots.filter { if (it.id == "ghost_bride") !viewModel.hasPhotographedGhostBride else true }
    }

    val activeOverlayObjects = remember(viewModel.hasPhotographedGhostBride) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasPhotographedGhostBride) {
            list.add(SceneOverlayObject("ghost_gone", "hotel_ghost_gone_overlay", Rect(727f, 1105f, 727f + 169f, 1105f + 161f)))
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
            imageName = "hotel_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "luggage" -> {
                        alertTitle = "Luggage on Cart"
                        alertMessage = "A cart of bags sits patiently — proof of new journeys starting from here."
                        showingAlert = true
                    }
                    "ghost_bride" -> activePhoto = Photo.banffSpringsHotel
                    "horse_drawn_carriage" -> {
                        alertTitle = "Horse-Drawn Carriage"
                        alertMessage = "The carriage waits in the snow as if expecting guests from another century."
                        showingAlert = true
                    }
                    "foot_bridge" -> {
                        alertTitle = "Foot Bridge"
                        alertMessage = "The bridge crosses into the hotel's winter quiet. Footprints vanish quickly here."
                        showingAlert = true
                    }
                    "main_doors" -> {
                        alertTitle = "Grand Entrance"
                        alertMessage = "The doors are impressive enough to make every visitor feel underdressed."
                        showingAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Banff Springs Hotel",
            locationSubtitle = "Ghosts in the grand old halls",
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