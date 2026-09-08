package com.vi.androidapp3.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import com.vi.androidapp3.viewmodel.GameViewModel

/** Progression stages for the curator endgame encounter. */
private enum class CuratorEndingPhase { PUZZLE, REWARDS }

/**
 * Endgame narrative screen at the curator's desk.
 * Checks for complete photo journal collection, hosts the final anagram puzzle,
 * and presents completion rewards.
 */
@Composable
fun CuratorEndingView(
    viewModel: GameViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var endingPhase by remember { mutableStateOf(CuratorEndingPhase.PUZZLE) }
    val foundAllPhotos = viewModel.photoCount == viewModel.totalPhotoCount

    val curatorDeskResId = remember {
        context.resources.getIdentifier("ending_curator_at_desk", "drawable", context.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF061833),
                        Color(0xFF145280),
                        Color(0xFF061833)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("The Curator's Desk", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

            if (curatorDeskResId != 0) {
                Image(
                    painter = painterResource(id = curatorDeskResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .shadow(16.dp)
                )
            }

            if (foundAllPhotos) {
                when (endingPhase) {
                    CuratorEndingPhase.PUZZLE -> {
                        Text(
                            text = "The curator studies your photographs in silence.\n\n“You found every missing piece,” she gasps. “Now tell me what the letters reveal.”",
                            color = Color.White.copy(alpha = 0.88f),
                            textAlign = TextAlign.Center
                        )

                        // Final anagram puzzle entry
                        LetterScrapPuzzleView(
                            letters = viewModel.discoveredCuratorLetters,
                            solution = "SASQUATCH",
                            onSolved = { SoundManager.shared.play(GameSound.CURATOR_SUCCESS, 0.85f)
                                endingPhase = CuratorEndingPhase.REWARDS },
                            onWrongAnswer = {SoundManager.shared.play(GameSound.CURATOR_WRONG, 0.85f)}
                        )

                        OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                            Text("Return to Museum")
                        }
                    }
                    CuratorEndingPhase.REWARDS -> {
                        CuratorRewardWrapUpView(viewModel = viewModel, onDismiss = onDismiss)
                    }
                }
            } else {
                // Incomplete journal state prompting the player to keep looking
                Text(
                    text = "The curator listens carefully as you describe the cave, the mine, and the impossible figure in the dark.\n\nBut when she spreads your journal photos across the desk, there are still gaps in the story.",
                    color = Color.White.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Photos Found: ${viewModel.photoCount}/${viewModel.totalPhotoCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )

                Text(
                    text = "Return to Banff and complete the historical photo journal.\n\nOnly then can you solve the curator's final question.",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Keep Exploring")
                }
            }
        }
    }
}

/** Displays completion summary, reward codes, and game restart dialog. */
@Composable
fun CuratorRewardWrapUpView(
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("The Museum is Saved", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

        // Narrative conclusion card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "“Sasquatch,” the curator murmurs.\n\nThe empty cave photograph. The small gold nugget burning like a secret in your coat pocket. Your photos and journal notes. And a bump on your noggin requiring ice!\n\nIt isn’t proof that can survive scrutiny.\nIt won’t open locked doors.\nIt won’t guide anyone back to the lair.\n\nBut it will ignite interest—\nwith a story so legendary people will come hoping for their own glimpses.\n\nThe museum is saved.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.88f),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Grand Prize Draw Entry Unlocked", fontWeight = FontWeight.Bold, color = Color.Yellow)
            }
        }

        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enter $5000 Grand Prize Draw")
        }

        // Summary of unlocked achievements and codes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("CONGRATULATIONS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)

            RewardRow(Icons.Default.CameraAlt, "Photo Journal Complete", "${viewModel.photoCount}/${viewModel.totalPhotoCount} historical photos captured")
            RewardRow(Icons.Default.Star, "Grand Prize Entry", "Curator puzzle solved")
            RewardRow(Icons.Default.Celebration, "Story of the Century", "You answered who guards the Lost Lemon Mine")
            RewardRow(Icons.Default.ConfirmationNumber, "Reward Code", viewModel.photoRewardCode)
            if (viewModel.hasFoundGoldNuggetInPocket) {
                RewardRow(Icons.Default.Star, "Unexpected Gift", "A Lost Lemon gold nugget souvenir")
            }
        }

        Button(
            onClick = { showExitDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exit")
        }
    }

    // Confirmation dialog before clearing game state
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Game?") },
            text = { Text("This will clear your progress and return you to the welcome screen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.resetForNewGame()
                        onDismiss()
                    }
                ) {
                    Text("Start Over", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/** Reusable row item for displaying reward achievements. */
@Composable
private fun RewardRow(icon: ImageVector, title: String, message: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            Text(message, color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp)
        }
    }
}