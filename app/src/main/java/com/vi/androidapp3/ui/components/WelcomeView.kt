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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.data.LocationId
import com.vi.androidapp3.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeView(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showingHowToPlay by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val bannerResId = remember {
        context.resources.getIdentifier("welcome_banner", "drawable", context.packageName)
            .takeIf { it != 0 }
            ?: context.resources.getIdentifier("welcomebanner", "drawable", context.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF061833),
                        Color(0xFF9FBCE5),
                        Color(0xFF145280)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "The Curator’s",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Banff Mystery",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(8.dp)
                )
                Text(
                    text = "A story-driven scavenger hunt through Banff",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center
                )
            }

            // Banner Image
            if (bannerResId != 0) {
                Image(
                    painter = painterResource(id = bannerResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, Color(0xFF2196F3), RoundedCornerShape(22.dp))
                )
            }

            // Story Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.48f), RoundedCornerShape(22.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Curator’s Message",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Research key historical sites across Banff, and photograph its history before the museum’s story disappears for good.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.86f),
                    lineHeight = 20.sp
                )
            }

            // Feature Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WelcomeFeatureRow(
                    icon = Icons.Default.Map,
                    titleTop = "Gather",
                    titleBottom = "Research",
                    modifier = Modifier.weight(1f)
                )
                WelcomeFeatureRow(
                    icon = Icons.Default.Search,
                    titleTop = "Collect",
                    titleBottom = "Items",
                    modifier = Modifier.weight(1f)
                )
                WelcomeFeatureRow(
                    icon = Icons.Default.CameraAlt,
                    titleTop = "Take",
                    titleBottom = "Photos",
                    modifier = Modifier.weight(1f)
                )
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.currentLocation = LocationId.MUSEUM_EXTERIOR
                        viewModel.hasStartedGame = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Yellow,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Begin Investigation", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showingHowToPlay = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("How to Play")
                }

                Text(
                    text = "Banff Park Museum Historical Scavenger Hunt",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.55f)
                )
            }
        }

        if (showingHowToPlay) {
            ModalBottomSheet(
                onDismissRequest = { showingHowToPlay = false },
                sheetState = sheetState,
                containerColor = Color(0xFF061833)
            ) {
                HowToPlaySheet(onDone = { showingHowToPlay = false })
            }
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: ImageVector,
    titleTop: String,
    titleBottom: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = titleTop,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = titleBottom,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HowToPlaySheet(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("How to Play", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = onDone) {
                Icon(Icons.Default.Close, contentDescription = "Done", tint = Color.White)
            }
        }

        InstructionSection("1. Use the Corkboard", "Inside the museum, tap the corkboard to choose story leads and visit Banff locations.")
        InstructionSection("2. Collect Tools and Notes", "Tap objects in each scene. Some investigation may require inventory items, such as a shovel or crowbar.")
        InstructionSection("3. Photograph Banff's History", "Many locations have photos. Find and take these photos to reveal puzzle clues and unlock discount rewards and endings.")
        InstructionSection("4. Solve the Curator’s Puzzle", "Each photo reveals one red-circled letter. Collect and unscramble all 9 letters to solve the curator’s puzzle and qualify for the grand prize draw.")
        InstructionSection("5. Submit Your Results", "At the end, submit your results. Photographing 5 to 6 photos unlocks a 10% discount. Photographing 7 to 9 unlocks a 20% discount. All 9 plus the curator’s puzzle solution unlocks the grand prize entry.")

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InstructionSection(title: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.Yellow, fontSize = 15.sp)
        Text(text, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 18.sp)
    }
}