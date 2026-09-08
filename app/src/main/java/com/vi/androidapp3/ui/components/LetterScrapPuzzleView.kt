package com.vi.androidapp3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

data class PuzzleLetterTile(
    val id: UUID = UUID.randomUUID(),
    val letter: String,
    val imageName: String
)

@Composable
fun LetterScrapPuzzleView(
    letters: String,
    solution: String,
    onSolved: () -> Unit,
    onWrongAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val availableTiles = remember { mutableStateListOf<PuzzleLetterTile>() }
    val answerSlots = remember { mutableStateListOf<PuzzleLetterTile?>() }
    var selectedTileId by remember { mutableStateOf<UUID?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    fun setupTiles() {
        val discoveredLetters = letters.uppercase().toList()
        val letterCounts = mutableMapOf<String, Int>()

        availableTiles.clear()
        discoveredLetters.forEach { char ->
            val l = char.toString()
            val occ = (letterCounts[l] ?: 0) + 1
            letterCounts[l] = occ
            val img = when (l) {
                "A" -> "corkboard_letter_a_$occ"
                "S" -> "corkboard_letter_s_$occ"
                "C" -> "corkboard_letter_c"
                "H" -> "corkboard_letter_h"
                "Q" -> "corkboard_letter_q"
                "T" -> "corkboard_letter_t"
                "U" -> "corkboard_letter_u"
                else -> "corkboard_letter_blank"
            }
            availableTiles.add(PuzzleLetterTile(letter = l, imageName = img))
        }

        answerSlots.clear()
        repeat(solution.length) { answerSlots.add(null) }
        selectedTileId = null
        message = null
    }

    LaunchedEffect(letters) {
        if (availableTiles.isEmpty() && answerSlots.isEmpty()) {
            setupTiles()
        }
    }

    val isComplete = answerSlots.isNotEmpty() && answerSlots.all { it != null }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Available Scraps Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Letter Scraps", fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                availableTiles.forEach { tile ->
                    LetterScrapTileView(
                        tile = tile,
                        size = 38,
                        isSelected = selectedTileId == tile.id,
                        onClick = {
                            message = null
                            selectedTileId = if (selectedTileId == tile.id) null else tile.id
                        }
                    )
                }
            }
        }

        Text("Arrange the letter scraps.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Tap a scrap, then tap a space.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))

        // Answer Line Slots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            answerSlots.indices.forEach { index ->
                val tile = answerSlots[index]
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable {
                            message = null
                            val selId = selectedTileId
                            if (selId != null) {
                                val availIdx = availableTiles.indexOfFirst { it.id == selId }
                                if (availIdx != -1) {
                                    val selTile = availableTiles.removeAt(availIdx)
                                    val existing = answerSlots[index]
                                    if (existing != null) availableTiles.add(existing)
                                    answerSlots[index] = selTile
                                    selectedTileId = null
                                    return@clickable
                                }
                            }
                            if (tile != null) {
                                answerSlots[index] = null
                                availableTiles.add(tile)
                                selectedTileId = null
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (tile != null) {
                        LetterScrapTileView(
                            tile = tile,
                            size = 38,
                            isSelected = selectedTileId == tile.id,
                            onClick = {
                                answerSlots[index] = null
                                availableTiles.add(tile)
                                selectedTileId = null
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val current = answerSlots.mapNotNull { it?.letter }.joinToString("")
                if (current.equals(solution, ignoreCase = true)) {
                    onSolved()
                } else {
                    message = "Not quite. Try rearranging the letters."
                    onWrongAnswer()
                }
            },
            enabled = isComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Answer")
        }

        OutlinedButton(
            onClick = { setupTiles() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Scraps")
        }

        message?.let {
            Text(it, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LetterScrapTileView(
    tile: PuzzleLetterTile,
    size: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(tile.imageName, "drawable", context.packageName)

    Box(
        modifier = Modifier
            .size(size.dp)
            .scale(if (isSelected) 1.12f else 1.0f)
            .shadow(if (isSelected) 8.dp else 4.dp, RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) Color.Yellow else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = tile.letter,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}