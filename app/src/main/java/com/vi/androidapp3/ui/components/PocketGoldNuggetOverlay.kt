package com.vi.androidapp3.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PocketGoldNuggetOverlay(
    onAddToInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = remember {
        context.resources.getIdentifier("item_lost_lemon_gold_nugget", "drawable", context.packageName)
    }

    val nuggetScale = remember { Animatable(0.75f) }
    val nuggetOpacity = remember { Animatable(0f) }
    val textOpacity = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        nuggetScale.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow))
        nuggetOpacity.animateTo(1f, tween(300))
        delay(450)
        textOpacity.animateTo(1f, tween(650))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .background(Color.Black.copy(alpha = 0.88f), RoundedCornerShape(26.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Something is in your pocket.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(145.dp)
                        .scale(nuggetScale.value)
                        .alpha(nuggetOpacity.value)
                )
            }

            Column(
                modifier = Modifier.alpha(textOpacity.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lost Lemon Gold Nugget",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )

                Text(
                    text = "It is real.\n\nWhatever happened beneath Tunnel Mountain, you brought something back.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.86f),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onAddToInventory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Nugget to Bag")
            }
        }
    }
}