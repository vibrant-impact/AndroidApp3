package com.vi.androidapp3.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class Snowflake(
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val opacity: Float,
    val fallSpeed: Float,
    val driftSpeed: Float,
    val driftAmount: Float,
    val phase: Float
)

@Composable
fun SnowfallOverlay(
    flakeCount: Int = 60,
    modifier: Modifier = Modifier
) {
    val flakes = remember(flakeCount) {
        List(flakeCount) { index ->
            Snowflake(
                xRatio = Random.nextFloat(),
                yRatio = Random.nextFloat(),
                size = Random.nextDouble(2.0, 6.0).toFloat(),
                opacity = Random.nextDouble(0.22, 0.78).toFloat(),
                fallSpeed = Random.nextDouble(18.0, 52.0).toFloat(),
                driftSpeed = Random.nextDouble(0.4, 1.3).toFloat(),
                driftAmount = Random.nextDouble(8.0, 34.0).toFloat(),
                phase = index * 0.7f
            )
        }
    }

    var timeSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            withFrameNanos { frameTimeNanos ->
                timeSeconds = (frameTimeNanos - startTime) / 1_000_000_000f
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        flakes.forEach { flake ->
            val drift = sin(timeSeconds * flake.driftSpeed + flake.phase) * flake.driftAmount
            val x = (flake.xRatio * w + drift).coerceIn(0f, w)
            val rawY = (flake.yRatio * h + (timeSeconds * flake.fallSpeed) % (h + 80f)) - 40f

            drawCircle(
                color = Color.White.copy(alpha = flake.opacity),
                radius = flake.size,
                center = Offset(x, rawY)
            )
        }
    }
}