package com.vi.androidapp3.data

import androidx.compose.ui.geometry.Rect

/**
 * Defines a tappable interactive bounding box mapped against base canvas coordinates (1290 x 2796).
 */
data class SceneHotspot(
    val id: String,
    val name: String,
    val rect: Rect
)