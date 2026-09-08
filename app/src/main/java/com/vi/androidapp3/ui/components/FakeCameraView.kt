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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CropFree
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.data.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FakeCameraView(
    photo: Photo,
    alreadyCaptured: Boolean,
    onCapture: (Photo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(photo.cameraImageName, "drawable", context.packageName)

    var hasTakenPhoto by remember { mutableStateOf(false) }
    val flashOpacity = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val showingPolaroid = alreadyCaptured || hasTakenPhoto

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color(0xFF141A21),
                        Color.Black
                    )
                )
            )
    ) {
        if (showingPolaroid) {
            // Polaroid Result
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        text = if (alreadyCaptured && !hasTakenPhoto) "Photo Already Collected" else "Photo Captured",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Polaroid Card
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .rotate(-2f)
                        .shadow(16.dp, RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = photo.photoName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Text(photo.photoName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("CLUE:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .border(3.dp, Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(photo.secretLetter, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }

                // Historical Note Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Historical Relevance", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F), fontSize = 16.sp)
                    Text(photo.historicalNote, color = Color.White.copy(alpha = 0.88f), fontSize = 14.sp)
                    Text("Find all 9 clues to solve the curator's puzzle.", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            // Viewfinder Screen
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
                    Text("PHOTO DOCUMENTATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
                }

                Text(photo.photoName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

                // Viewfinder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(4.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.CropFree,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(90.dp)
                    )
                }

                Text("Snap the Pic", fontSize = 12.sp, color = Color.White.copy(alpha = 0.55f))

                // Shutter Button
                Box(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .size(82.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            scope.launch {
                                flashOpacity.snapTo(1f)
                                delay(250)
                                onCapture(photo)
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

        // Camera Flash Overlay
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