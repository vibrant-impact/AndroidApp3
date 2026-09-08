package com.vi.androidapp3.ui.locations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.AmbientSound
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.InventoryItem
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.Photo
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.data.SceneOverlayObject
import com.vi.androidapp3.ui.components.CombinationLockView
import com.vi.androidapp3.ui.components.FakeCameraView
import com.vi.androidapp3.ui.components.HotspotZoomOverlay
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.ItemCollectedOverlay
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.ui.components.PocketGoldNuggetOverlay
import com.vi.androidapp3.ui.components.SnowfallOverlay
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MuseumExteriorZoomOverlay {
    MAILBOX_CLOSED, MAILBOX_OPEN, SHOVEL, SIGN, DOOR
}

/**
 * Primary arrival scene screen outside the Banff Park Museum.
 * Features opening mailbox letter notes, shovel acquisition, museum door combination lock (1903),
 * footprint photo capture, and post-lair wake-up sequences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseumExteriorView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }
    val scope = rememberCoroutineScope()

    var showingDoorLock by remember { mutableStateOf(false) }
    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }

    var simpleAlertTitle by remember { mutableStateOf("") }
    var simpleAlertMessage by remember { mutableStateOf("") }
    var showingSimpleAlert by remember { mutableStateOf(false) }

    var activePhoto by remember { mutableStateOf<Photo?>(null) }
    var activeZoomOverlay by remember { mutableStateOf<MuseumExteriorZoomOverlay?>(null) }
    var collectedItemOverlay by remember { mutableStateOf<InventoryItem?>(null) }

    val wakeTextOpacity = remember { Animatable(0f) }
    var isShowingWakeUpText by remember { mutableStateOf(false) }
    var isShowingHeadHurtsAlert by remember { mutableStateOf(false) }
    var isShowingPocketGoldOverlay by remember { mutableStateOf(false) }

    val rawHotspots = remember {
        listOf(
            SceneHotspot("mailbox", "Mailbox", Rect(13f, 1871f, 13f + 354f, 1871f + 245f)),
            SceneHotspot("footprint", "Massive Mysterious Footprint", Rect(340f, 2390f, 340f + 317f, 2390f + 328f)),
            SceneHotspot("shovel", "Small Shovel", Rect(992f, 2291f, 992f + 197f, 2291f + 266f)),
            SceneHotspot("sign", "Museum Sign", Rect(495f, 1303f, 495f + 471f, 1303f + 222f)),
            SceneHotspot("door", "Museum Door", Rect(719f, 1674f, 719f + 149f, 1674f + 206f)),
            SceneHotspot("sled", "Sled", Rect(854f, 1797f, 854f + 189f, 1797f + 291f)),
            SceneHotspot("rabbit", "Rabbit", Rect(68f, 1713f, 68f + 116f, 1713f + 124f)),
            SceneHotspot("birdhouse", "Birdhouse", Rect(1136f, 701f, 1136f + 151f, 701f + 184f))
        )
    }

    val activeHotspots = remember(viewModel.hasCollectedShovel) {
        rawHotspots.filter { if (it.id == "shovel") !viewModel.hasCollectedShovel else true }
    }

    val activeOverlayObjects = remember(viewModel.hasOpenedMailbox, viewModel.hasCollectedShovel) {
        val list = mutableListOf<SceneOverlayObject>()
        if (viewModel.hasOpenedMailbox) {
            list.add(SceneOverlayObject("mailbox_open", "museum_mailbox_open_overlay", Rect(187f, 1909f, 187f + 253f, 1909f + 312f)))
        }
        if (viewModel.hasCollectedShovel) {
            list.add(SceneOverlayObject("shovel_gone", "museum_shovel_gone_overlay", Rect(970f, 2289f, 970f + 236f, 2289f + 287f)))
        }
        list
    }

    // Handles post-lair return fade-in text and pocket gold discovery triggers
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.SNOWY_EXTERIOR, 1.0f)

        if (viewModel.shouldShowMuseumWakeUpAfterLair) {
            viewModel.shouldShowMuseumWakeUpAfterLair = false
            isShowingWakeUpText = true
            wakeTextOpacity.snapTo(0f)

            delay(300)
            wakeTextOpacity.animateTo(1f, tween(800))
            delay(2000)
            wakeTextOpacity.animateTo(0f, tween(800))
            isShowingWakeUpText = false

            if (!viewModel.hasFoundGoldNuggetInPocket) {
                delay(300)
                isShowingHeadHurtsAlert = true
            }
        }
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
            imageName = "museum_exterior_base",
            canvasSize = canvasSize,
            hotspots = activeHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "mailbox" -> activeZoomOverlay = if (viewModel.hasOpenedMailbox) MuseumExteriorZoomOverlay.MAILBOX_OPEN else MuseumExteriorZoomOverlay.MAILBOX_CLOSED
                    "shovel" -> activeZoomOverlay = MuseumExteriorZoomOverlay.SHOVEL
                    "footprint" -> activePhoto = Photo.museumExterior
                    "sign" -> activeZoomOverlay = MuseumExteriorZoomOverlay.SIGN
                    "door" -> {
                        if (viewModel.isMuseumDoorUnlocked) {
                            SoundManager.shared.play(GameSound.DOOR_UNLOCK, 0.7f)
                            viewModel.currentLocation = LocationId.MUSEUM_INTERIOR
                        } else {
                            activeZoomOverlay = MuseumExteriorZoomOverlay.DOOR
                        }
                    }
                    "sled" -> {
                        simpleAlertTitle = "Old Sled"
                        simpleAlertMessage = "An old wooden sled rests in the snow, worn smooth from years of winter use."
                        showingSimpleAlert = true
                    }
                    "rabbit" -> {
                        simpleAlertTitle = "Snowshoe Hare"
                        simpleAlertMessage = "A snowshoe hare watches you from the edge of the museum grounds, perfectly still against the winter quiet."
                        showingSimpleAlert = true
                    }
                    "birdhouse" -> {
                        simpleAlertTitle = "Birdhouse"
                        simpleAlertMessage = "A tiny birdhouse hangs above the snow, its entrance rimmed with frost."
                        showingSimpleAlert = true
                    }
                }
            }
        )

        SnowfallOverlay()

        TopHUDView(
            locationTitle = "Discover Banff",
            locationSubtitle = "Legends and Lore",
            onBagTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingInventory = true
            },
            onJournalTapped = {
                SoundManager.shared.play(GameSound.CLICK, 0.5f)
                showingJournal = true
            }
        )

        when (activeZoomOverlay) {
            MuseumExteriorZoomOverlay.MAILBOX_CLOSED -> {
                HotspotZoomOverlay(
                    title = "Mailbox",
                    imageName = "zoom_museum_mailbox_open",
                    description = "There's a letter inside with your name on it.",
                    primaryButtonTitle = "Read Letter",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.hasOpenedMailbox = true
                        activeZoomOverlay = MuseumExteriorZoomOverlay.MAILBOX_OPEN
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            MuseumExteriorZoomOverlay.MAILBOX_OPEN -> {
                HotspotZoomOverlay(
                    title = "The Curator's Note",
                    imageName = "zoom_museum_mailbox_open",
                    description = "Make your way through Banff and gather what you need to write the ultimate story — one that can revive public interest and save the museum from closure.\n\nI believe you are the one who can answer the question I never could:\nWho guards the Lost Lemon Mine?\n\nHistory holds the key to the door.\n— The Museum Curator",
                    primaryButtonTitle = "Close",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        viewModel.hasReadMailboxNote = true
                        activeZoomOverlay = null
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            MuseumExteriorZoomOverlay.SHOVEL -> {
                HotspotZoomOverlay(
                    title = "Small Shovel",
                    imageName = "zoom_museum_shovel",
                    description = "A small metal shovel leans in the snow. It could help dig through packed drifts.",
                    primaryButtonTitle = "Take Shovel",
                    onPrimaryAction = {
                        SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                        viewModel.collectShovel()
                        activeZoomOverlay = null
                        collectedItemOverlay = InventoryItem.SMALL_SHOVEL
                    },
                    onClose = {
                        SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                        activeZoomOverlay = null
                    }
                )
            }
            MuseumExteriorZoomOverlay.SIGN -> {
                HotspotZoomOverlay(
                    title = "Museum Sign",
                    imageName = "zoom_museum_sign",
                    description = "The sign marks the Banff Park Museum, built in 1903. The date feels important.",
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
            MuseumExteriorZoomOverlay.DOOR -> {
                HotspotZoomOverlay(
                    title = "Front Door",
                    imageName = "zoom_museum_door",
                    description = "The museum door is locked with an old number code. The curator's note said history holds the key.",
                    primaryButtonTitle = "Try the Lock",
                    onPrimaryAction = {
                        activeZoomOverlay = null
                        showingDoorLock = true
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

        if (isShowingWakeUpText) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(wakeTextOpacity.value),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.75f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Your eyes open slowly.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Snowlight. Cold air. The museum steps.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("You are back in Banff.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }

        if (isShowingPocketGoldOverlay) {
            PocketGoldNuggetOverlay(
                onAddToInventory = {
                    SoundManager.shared.play(GameSound.ITEM_COLLECT, 0.85f)
                    viewModel.collectInventoryItem(InventoryItem.LOST_LEMON_GOLD_NUGGET)
                    isShowingPocketGoldOverlay = false
                    collectedItemOverlay = InventoryItem.LOST_LEMON_GOLD_NUGGET
                }
            )
        }
    }

    if (showingDoorLock) {
        ModalBottomSheet(onDismissRequest = { showingDoorLock = false }) {
            CombinationLockView(
                correctCode = "1903",
                onUnlock = {
                    SoundManager.shared.play(GameSound.DOOR_UNLOCK, 0.8f)
                    viewModel.isMuseumDoorUnlocked = true
                    showingDoorLock = false
                    scope.launch {
                        delay(350)
                        viewModel.currentLocation = LocationId.MUSEUM_INTERIOR
                    }
                },
                onDismiss = {
                    SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                    showingDoorLock = false
                }
            )
        }
    }

    if (showingInventory) {
        ModalBottomSheet(onDismissRequest = { showingInventory = false }) {
            InventoryView(
                items = viewModel.inventory,
                onDismiss = {
                    SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                    showingInventory = false
                }
            )
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
                SoundManager.shared.play(GameSound.CAMERA_FLASH, 0.45f)
            },
            onDismiss = {
                SoundManager.shared.play(GameSound.CLOSE, 0.45f)
                activePhoto = null }
        )
    }

    if (showingSimpleAlert) {
        AlertDialog(
            onDismissRequest = { showingSimpleAlert = false },
            title = { Text(simpleAlertTitle) },
            text = { Text(simpleAlertMessage) },
            confirmButton = { TextButton(onClick = { showingSimpleAlert = false }) { Text("OK") } }
        )
    }

    if (isShowingHeadHurtsAlert) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Your head aches.") },
            text = { Text("Cold stone. Falling ice. A shadow in the dark.\n\nWas it a dream?\n\nYou swear you saw Bigfoot… and the Lost Lemon Mine.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        isShowingHeadHurtsAlert = false
                        SoundManager.shared.play(GameSound.CLICK, 0.5f)
                        scope.launch {
                            delay(250)
                            isShowingPocketGoldOverlay = true
                        }
                    }
                ) { Text("Check Pocket") }
            }
        )
    }
}