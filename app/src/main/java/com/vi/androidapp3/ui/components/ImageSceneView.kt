package com.vi.androidapp3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.data.SceneHotspot
import com.vi.androidapp3.data.SceneOverlayObject
import kotlin.math.max
import kotlin.math.roundToInt

data class ImageLayout(
    val scale: Float,
    val origin: Offset,
    val renderedSize: Size
)

@Composable
fun ImageSceneView(
    imageName: String,
    canvasSize: Size,
    hotspots: List<SceneHotspot>,
    overlayObjects: List<SceneOverlayObject> = emptyList(),
    showDebugHotspots: Boolean = false,
    onHotspotTapped: (SceneHotspot) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val baseImageResId = remember(imageName) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val containerWidth = constraints.maxWidth.toFloat()
        val containerHeight = constraints.maxHeight.toFloat()

        val scale = max(
            containerWidth / canvasSize.width,
            containerHeight / canvasSize.height
        )
        val renderedWidth = canvasSize.width * scale
        val renderedHeight = canvasSize.height * scale
        val originX = (containerWidth - renderedWidth) / 2f
        val originY = (containerHeight - renderedHeight) / 2f
        val layout = ImageLayout(scale, Offset(originX, originY), Size(renderedWidth, renderedHeight))

        // Base Scene Image
        if (baseImageResId != 0) {
            Image(
                painter = painterResource(id = baseImageResId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .offset { IntOffset(originX.roundToInt(), originY.roundToInt()) }
                    .size(
                        width = (renderedWidth / LocalContext.current.resources.displayMetrics.density).dp,
                        height = (renderedHeight / LocalContext.current.resources.displayMetrics.density).dp
                    )
            )
        }

        // Overlay Visual State Objects
        overlayObjects.forEach { overlay ->
            val overlayResId = context.resources.getIdentifier(overlay.imageName, "drawable", context.packageName)
            if (overlayResId != 0) {
                val scaledRect = scaleRect(overlay.rect, layout)
                Image(
                    painter = painterResource(id = overlayResId),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .offset { IntOffset(scaledRect.left.roundToInt(), scaledRect.top.roundToInt()) }
                        .size(
                            width = (scaledRect.width / LocalContext.current.resources.displayMetrics.density).dp,
                            height = (scaledRect.height / LocalContext.current.resources.displayMetrics.density).dp
                        )
                )
            }
        }

        // Hotspot clickable bounds
        hotspots.forEach { hotspot ->
            val scaledRect = scaleRect(hotspot.rect, layout)
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .offset { IntOffset(scaledRect.left.roundToInt(), scaledRect.top.roundToInt()) }
                    .size(
                        width = (scaledRect.width / LocalContext.current.resources.displayMetrics.density).dp,
                        height = (scaledRect.height / LocalContext.current.resources.displayMetrics.density).dp
                    )
                    .background(
                        if (showDebugHotspots) Color.Red.copy(alpha = 0.28f)
                        else Color.Transparent
                    )
                    .then(
                        if (showDebugHotspots) Modifier.border(2.dp, Color.Yellow)
                        else Modifier
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onHotspotTapped(hotspot)
                    }
            ) {
                if (showDebugHotspots) {
                    Text(
                        text = hotspot.name,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

private fun scaleRect(rect: Rect, layout: ImageLayout): Rect {
    return Rect(
        left = layout.origin.x + rect.left * layout.scale,
        top = layout.origin.y + rect.top * layout.scale,
        right = layout.origin.x + rect.right * layout.scale,
        bottom = layout.origin.y + rect.bottom * layout.scale
    )
}