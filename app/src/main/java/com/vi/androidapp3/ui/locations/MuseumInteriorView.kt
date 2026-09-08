package com.vi.androidapp3.ui.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.AmbientSound
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.ui.components.CorkboardFullScreenView
import com.vi.androidapp3.ui.components.CuratorEndingView
import com.vi.androidapp3.ui.components.ImageSceneView
import com.vi.androidapp3.ui.components.InventoryView
import com.vi.androidapp3.ui.components.JournalView
import com.vi.androidapp3.viewmodel.GameViewModel

/**
 * Scene screen for the interior of the Banff Park Museum (Main Hub).
 * Features crackling fireplace ambience, exhibits, visitor logbooks,
 * access to the Corkboard investigation map, and dialogue with the curator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseumInteriorView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }

    var showingInventory by remember { mutableStateOf(false) }
    var showingJournal by remember { mutableStateOf(false) }
    var showingCorkboardCloseup by remember { mutableStateOf(false) }
    var showingCuratorEnding by remember { mutableStateOf(false) }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var showingAlert by remember { mutableStateOf(false) }

    val hotspots = remember {
        listOf(
            SceneHotspot("corkboard", "Corkboard Hub", Rect(110f, 1444f, 110f + 299f, 1444f + 437f)),
            SceneHotspot("curator", "Museum Curator", Rect(37f, 1877f, 37f + 234f, 1877f + 195f)),
            SceneHotspot("wild_bill_exhibit", "Wild Bill Peyto Exhibit", Rect(700f, 1648f, 700f + 386f, 1648f + 424f)),
            SceneHotspot("logbook", "Visitor Logbook", Rect(198f, 2044f, 198f + 241f, 2044f + 102f)),
            SceneHotspot("rotary_phone", "Rotary Phone", Rect(391f, 1954f, 391f + 130f, 1954f + 101f)),
            SceneHotspot("grizzly_display", "Grizzly Display", Rect(589f, 1365f, 589f + 209f, 1365f + 195f)),
            SceneHotspot("porcupine", "Porcupine", Rect(912f, 1485f, 912f + 173f, 1485f + 151f)),
            SceneHotspot("squirrel", "Squirrel", Rect(773f, 1511f, 773f + 108f, 1511f + 111f)),
            SceneHotspot("fireplace", "Fireplace", Rect(1118f, 1364f, 1118f + 147f, 1364f + 159f)),
            SceneHotspot("canoe", "Hanging Canoe", Rect(190f, 452f, 190f + 519f, 452f + 465f)),
            SceneHotspot("mountain_goat", "Mountain Goat", Rect(393f, 1141f, 393f + 171f, 1141f + 146f)),
            SceneHotspot("moose_head", "Moose Head", Rect(733f, 1031f, 733f + 191f, 1031f + 199f)),
            SceneHotspot("raccoon_display", "Raccoon Display", Rect(256f, 1255f, 256f + 168f, 1255f + 213f)),
            SceneHotspot("mineral_gems_display", "Mineral Gems Display", Rect(491f, 1533f, 491f + 206f, 1533f + 195f)),
            SceneHotspot("owls", "Owls", Rect(581f, 1161f, 581f + 162f, 1161f + 210f)),
            SceneHotspot("coal_mining_display", "Coal Mining Display", Rect(1147f, 1597f, 1147f + 143f, 1597f + 339f))
        )
    }

    // Manage interior fireplace ambient sound lifecycle
    LaunchedEffect(Unit) {
        SoundManager.shared.stopAllAmbience()
        SoundManager.shared.playAmbience(AmbientSound.FIREPLACE, 0.85f)
    }

    DisposableEffect(Unit) {
        onDispose {
            SoundManager.shared.stopAmbience(AmbientSound.FIREPLACE)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "museum_interior_base",
            canvasSize = canvasSize,
            hotspots = hotspots,
            overlayObjects = emptyList(),
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                SoundManager.shared.play(GameSound.TAP, 0.4f)
                when (hotspot.id) {
                    "corkboard" -> showingCorkboardCloseup = true
                    "curator" -> {
                        if (viewModel.hasReturnedFromBigfootLair) {
                            showingCuratorEnding = true
                        } else {
                            alertTitle = "The Museum Curator"
                            alertMessage = "The curator looks up from her desk.\n\n“Save the museum with the story of the century. Find the missing pieces, follow the history, and answer the question I never could.”\n\nWho guards the Lost Lemon Mine?"
                            showingAlert = true
                        }
                    }
                    "wild_bill_exhibit" -> {
                        alertTitle = "Wild Bill Peyto"
                        alertMessage = "Bill Peyto, aka 'Wild Bill' was a legendary Banff guide, outfitter, and park warden. His photograph prominently marks the entrance to the town. Peyto Glacier on the Continental Divide and Peyto Lake are named in his honour."
                        showingAlert = true
                    }
                    "logbook" -> {
                        alertTitle = "Visitor Logbook"
                        alertMessage = "Visitors logged their names here; doodles outnumber signatures three to one—art wins."
                        showingAlert = true
                    }
                    "rotary_phone" -> {
                        alertTitle = "Rotary Phone"
                        alertMessage = "So outdated it’s charming. Dialing this relic would take a decade."
                        showingAlert = true
                    }
                    "grizzly_display" -> {
                        alertTitle = "Grizzly Display"
                        alertMessage = "The grizzly seems enormous until you remember the curator's question. Maybe not every giant in the Rockies is a bear."
                        showingAlert = true
                    }
                    "porcupine" -> {
                        alertTitle = "Porcupine"
                        alertMessage = "This porcupine looks ready to lecture you—spines up, attitude sharper than its needles."
                        showingAlert = true
                    }
                    "squirrel" -> {
                        alertTitle = "Squirrel"
                        alertMessage = "The squirrel has the expression of someone who has hidden several important things and forgotten where."
                        showingAlert = true
                    }
                    "fireplace" -> {
                        alertTitle = "Fireplace"
                        alertMessage = "Warm light, gentle crackle—like it was built to shelter winter tails and tales."
                        showingAlert = true
                    }
                    "canoe" -> {
                        alertTitle = "Hanging Canoe"
                        alertMessage = "The canoe hangs above the museum floor, a reminder that trails through Banff were never only on land."
                        showingAlert = true
                    }
                    "mountain_goat" -> {
                        alertTitle = "Mountain Goat"
                        alertMessage = "The mountain goat looks perfectly at home on impossible cliffs. Some creatures belong where people struggle to follow."
                        showingAlert = true
                    }
                    "moose_head" -> {
                        alertTitle = "Moose Head"
                        alertMessage = "The moose stares down with calm authority. It has seen many tourists make poor footwear decisions."
                        showingAlert = true
                    }
                    "raccoon_display" -> {
                        alertTitle = "Raccoon Display"
                        alertMessage = "Raccoons never forget a snack. This one’s stuffed, but the mischief still feels alive."
                        showingAlert = true
                    }
                    "mineral_gems_display" -> {
                        alertTitle = "Mineral Gems"
                        alertMessage = "The stones catch the light. Banff's mountains hide beauty, pressure, and old geological secrets."
                        showingAlert = true
                    }
                    "owls" -> {
                        alertTitle = "Owls"
                        alertMessage = "The owls seem to know exactly what you are doing and have chosen not to interfere."
                        showingAlert = true
                    }
                    "coal_mining_display" -> {
                        alertTitle = "Coal Mining Display"
                        alertMessage = "The mining display is a reminder that once people believe there is wealth underground, they rarely leave quietly."
                        showingAlert = true
                    }
                }
            }
        )

        // Readability Gradients
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.72f), Color.Transparent)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(280.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))))
        )

        // Top and Bottom HUD Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Banff Park Museum", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("A Historical Treasure Trove", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showingJournal = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Journal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showingInventory = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Bag", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    SoundManager.shared.play(GameSound.WOOSH, 0.85f)
                    viewModel.currentLocation = LocationId.MUSEUM_EXTERIOR },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Outside")
            }
        }
    }

    if (showingCorkboardCloseup) {
        CorkboardFullScreenView(viewModel = viewModel, onDismiss = { showingCorkboardCloseup = false })
    }

    if (showingCuratorEnding) {
        CuratorEndingView(viewModel = viewModel, onDismiss = { showingCuratorEnding = false })
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