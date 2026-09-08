package com.vi.androidapp3.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interactive camera viewfinder used inside Bigfoot's Lair.
 * Simulates snapping evidence of Bigfoot, triggering a flash animation,
 * and revealing an empty cave photograph as Bigfoot vanishes.
 */
@Composable
fun BigfootEvidenceCameraView(
    onCapture: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasTakenPhoto by remember { mutableStateOf(false) }
    val flashOpacity = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Resolve drawable resource IDs for before and after capture states
    val lairImageResId = remember {
        context.resources.getIdentifier("camera_bigfoot_in_lair", "drawable", context.packageName)
    }
    val emptyCaveImageResId = remember {
        context.resources.getIdentifier("camera_bigfoot_empty_cave", "drawable", context.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF14141A), Color.Black)
                )
            )
    ) {
        if (hasTakenPhoto) {
            // Post-capture polaroid result showing the creature disappeared
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Photo Captured",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .rotate(2f)
                        .shadow(16.dp, RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (emptyCaveImageResId != 0) {
                        Image(
                            painter = painterResource(id = emptyCaveImageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Text("Empty Cave", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                    Text(
                        text = "When the image settles, Bigfoot is gone.\n\nThe cave is empty.\n\nOnly a pale blur of breath hangs near the edge of the frame.",
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        onCapture()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lower Camera")
                }
            }
        } else {
            // Active camera viewfinder screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        text = "EVIDENCE PHOTO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Viewfinder framing Bigfoot in the lair
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(4.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (lairImageResId != 0) {
                        Image(
                            painter = painterResource(id = lairImageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text("Bigfoot", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = "For one impossible second, he stands still.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                }

                // Shutter button trigger
                Box(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .size(82.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            scope.launch {
                                flashOpacity.snapTo(1f)
                                delay(250)
                                hasTakenPhoto = true
                                flashOpacity.animateTo(0f, tween(450))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .border(4.dp, Color.Black.copy(alpha = 0.35f), CircleShape)
                    )
                }
            }
        }

        // Screen-wide white camera flash overlay
        if (flashOpacity.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flashOpacity.value)
                    .background(Color.White)
            )
        }
    }
}