package com.vi.androidapp3.data

import androidx.compose.ui.geometry.Rect

/**
 * Defines a visual state overlay (such as an opened box, dug hole, or missing item)
 * rendered at fixed canvas coordinates when world state changes.
 */
data class SceneOverlayObject(
    val id: String,
    val imageName: String,
    val rect: Rect
)