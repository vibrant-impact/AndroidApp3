package com.vi.androidapp3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.data.Photo

@Composable
fun JournalView(
    journalPhotos: List<Photo>,
    photoCount: Int,
    totalPhotoCount: Int,
    photoRewardCode: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF16181D))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Journal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Progress Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(Color(0xFF22262F), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Historical photos captured:", fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("$photoCount / $totalPhotoCount", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Divider(color = Color.White.copy(alpha = 0.1f))

            Text("Capture at least 5 photos to unlock bonuses.", fontSize = 13.sp, color = Color.LightGray)
            Text("Find all 9 and solve the curator's puzzle to unlock the $5000 grand prize draw entry.", fontSize = 13.sp, color = Color.LightGray)

            Divider(color = Color.White.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Discount Code:", fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = photoRewardCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (photoRewardCode == "LOCKED") Color.Gray else Color(0xFF66BB6A)
                )
            }
        }

        if (journalPhotos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.size(16.dp))
                Text("No Journal Photos Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "Take photos of important Banff history hotspots to collect notes and hidden letters.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(journalPhotos) { photo ->
                    val imageResId = context.resources.getIdentifier(photo.cameraImageName, "drawable", context.packageName)
                    val rotation = when (photo.id) {
                        Photo.museumExterior.id -> -2f
                        Photo.bowFalls.id -> 1.5f
                        Photo.caveAndBasin.id -> -1f
                        Photo.banffSpringsHotel.id -> 2f
                        Photo.downtownBanff.id -> -1.5f
                        Photo.hotSprings.id -> 1f
                        Photo.sulphurMountain.id -> -2f
                        Photo.lakeMinnewanka.id -> 1.8f
                        Photo.tunnelMountain.id -> -0.8f
                        else -> 0f
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF22262F), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Mini Polaroid
                        Column(
                            modifier = Modifier
                                .rotate(rotation)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (imageResId != 0) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = photo.photoName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(68.dp)
                                )
                            }
                            Text(
                                text = photo.secretLetter,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = photo.photoName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(photo.secretLetter, color = Color(0xFFFF9800), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            Text(photo.historicalNote, fontSize = 13.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}