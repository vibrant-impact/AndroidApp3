package com.vi.androidapp3.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.data.Photo
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.data.SceneOverlayObject
import com.vi.androidapp3.ui.hud.TopHUDView
import com.vi.androidapp3.viewmodel.GameViewModel

private data class CorkboardLocationHotspot(
    val id: String,
    val name: String,
    val location: LocationId,
    val rect: Rect
)

private data class CorkboardLetterOverlay(
    val id: String,
    val imageName: String,
    val photo: Photo,
    val rect: Rect
)

@Composable
fun CorkboardFullScreenView(
    viewModel: GameViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canvasSize = remember { Size(1290f, 2796f) }

    val locationHotspots = remember {
        listOf(
            CorkboardLocationHotspot("cave_and_basin", "Cave and Basin", LocationId.CAVE_AND_BASIN, Rect(167f, 1234f, 167f + 265f, 1234f + 264f)),
            CorkboardLocationHotspot("hot_springs", "Upper Hot Springs", LocationId.HOT_SPRINGS, Rect(525f, 1213f, 525f + 249f, 1213f + 290f)),
            CorkboardLocationHotspot("lake_minnewanka", "Lake Minnewanka", LocationId.LAKE_MINNEWANKA, Rect(852f, 1244f, 852f + 257f, 1244f + 280f)),
            CorkboardLocationHotspot("sulphur_mountain", "Sulphur Mountain", LocationId.SULPHUR_MOUNTAIN, Rect(158f, 1547f, 158f + 256f, 1547f + 265f)),
            CorkboardLocationHotspot("bow_falls", "Bow Falls", LocationId.BOW_FALLS, Rect(843f, 1556f, 843f + 252f, 1556f + 262f)),
            CorkboardLocationHotspot("tunnel_mountain", "Tunnel Mountain", LocationId.TUNNEL_MOUNTAIN, Rect(189f, 1853f, 189f + 268f, 1853f + 269f)),
            CorkboardLocationHotspot("banff_springs_hotel", "Banff Springs Hotel", LocationId.BANFF_SPRINGS_HOTEL, Rect(506f, 1847f, 506f + 276f, 1847f + 280f)),
            CorkboardLocationHotspot("downtown_banff", "Downtown Banff", LocationId.DOWNTOWN_BANFF, Rect(843f, 1858f, 843f + 265f, 1858f + 268f))
        )
    }

    val letterOverlays = remember {
        listOf(
            CorkboardLetterOverlay("letter_a_museum_exterior", "corkboard_letter_a_1", Photo.museumExterior, Rect(128f, 1025f, 128f + 121f, 1025f + 156f)),
            CorkboardLetterOverlay("letter_s_bow_falls", "corkboard_letter_s_1", Photo.bowFalls, Rect(245f, 1059f, 245f + 117f, 1059f + 152f)),
            CorkboardLetterOverlay("letter_t_cave_and_basin", "corkboard_letter_t", Photo.caveAndBasin, Rect(358f, 1017f, 358f + 108f, 1017f + 146f)),
            CorkboardLetterOverlay("letter_c_banff_springs_hotel", "corkboard_letter_c", Photo.banffSpringsHotel, Rect(465f, 1045f, 465f + 112f, 1045f + 154f)),
            CorkboardLetterOverlay("letter_q_downtown_banff", "corkboard_letter_q", Photo.downtownBanff, Rect(576f, 1057f, 576f + 124f, 1057f + 148f)),
            CorkboardLetterOverlay("letter_h_hot_springs", "corkboard_letter_h", Photo.hotSprings, Rect(663f, 1013f, 663f + 142f, 1013f + 168f)),
            CorkboardLetterOverlay("letter_u_sulphur_mountain", "corkboard_letter_u", Photo.sulphurMountain, Rect(794f, 1028f, 794f + 139f, 1028f + 180f)),
            CorkboardLetterOverlay("letter_s_lake_minnewanka", "corkboard_letter_s_2", Photo.lakeMinnewanka, Rect(907f, 1065f, 907f + 126f, 1065f + 154f)),
            CorkboardLetterOverlay("letter_a_tunnel_mountain", "corkboard_letter_a_2", Photo.tunnelMountain, Rect(986f, 1023f, 986f + 143f, 1023f + 175f))
        )
    }

    val sceneHotspots = remember(locationHotspots) {
        locationHotspots.map { SceneHotspot(it.id, it.name, it.rect) }
    }

    val activeOverlayObjects = remember(viewModel.photoIDs) {
        letterOverlays
            .filter { viewModel.hasPhoto(it.photo) }
            .map { SceneOverlayObject(it.id, it.imageName, it.rect) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ImageSceneView(
            imageName = "corkboard_base",
            canvasSize = canvasSize,
            hotspots = sceneHotspots,
            overlayObjects = activeOverlayObjects,
            showDebugHotspots = false,
            onHotspotTapped = { hotspot ->
                val marker = locationHotspots.firstOrNull { it.id == hotspot.id }
                if (marker != null) {
                    SoundManager.shared.play(GameSound.LOCATION_TRAVEL_TINKLE, 0.6f)
                    viewModel.currentLocation = marker.location
                    onDismiss()
                }
            }
        )

        TopHUDView(
            locationTitle = "The Curator's Corkboard",
            locationSubtitle = "Save the museum with the story of the century",
            showsBagButton = false,
            showsJournalButton = false
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}