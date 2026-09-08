package com.vi.androidapp3

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vi.androidapp3.audio.HapticsManager
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.ui.components.WelcomeView
import com.vi.androidapp3.ui.locations.*
import com.vi.androidapp3.viewmodel.GameViewModel

/**
 * Main application entry point. Initializes audio and haptic subsystems,
 * configures volume streams, and sets up the root Jetpack Compose UI content.
 */
class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Route hardware volume keys directly to game audio stream
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Initialize audio and haptic singletons
        SoundManager.initialize(applicationContext)
        HapticsManager.initialize(applicationContext)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    if (!gameViewModel.hasStartedGame) {
                        WelcomeView(viewModel = gameViewModel)
                    } else {
                        CurrentLocationRouter(viewModel = gameViewModel)
                    }
                }
            }
        }
    }
}

/** Root location router switching active scene composables based on the ViewModel state. */
@Composable
fun CurrentLocationRouter(viewModel: GameViewModel) {
    when (viewModel.currentLocation) {
        LocationId.MUSEUM_EXTERIOR -> MuseumExteriorView(viewModel)
        LocationId.MUSEUM_INTERIOR -> MuseumInteriorView(viewModel)
        LocationId.BOW_FALLS -> BowFallsView(viewModel)
        LocationId.CAVE_AND_BASIN -> CaveAndBasinView(viewModel)
        LocationId.BANFF_SPRINGS_HOTEL -> BanffSpringsHotelView(viewModel)
        LocationId.DOWNTOWN_BANFF -> DowntownBanffView(viewModel)
        LocationId.HOT_SPRINGS -> HotSpringsView(viewModel)
        LocationId.SULPHUR_MOUNTAIN -> SulphurMountainView(viewModel)
        LocationId.OBSERVATORY -> ObservatoryView(viewModel)
        LocationId.LAKE_MINNEWANKA -> LakeMinnewankaView(viewModel)
        LocationId.TUNNEL_MOUNTAIN -> TunnelMountainView(viewModel)
        LocationId.BIGFOOT_LAIR -> BigfootLairView(viewModel)
    }
}