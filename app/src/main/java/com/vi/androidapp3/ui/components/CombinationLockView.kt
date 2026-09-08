package com.vi.androidapp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.audio.GameSound
import com.vi.androidapp3.audio.SoundManager

/** Single digit column dial for the combination lock tumbler. */
@Composable
fun LockDial(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF2A2E35), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onIncrement, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment", tint = Color.White)
        }

        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        IconButton(onClick = onDecrement, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement", tint = Color.White)
        }
    }
}

/**
 * Modal bottom sheet presenting an old four-digit tumbler lock for the museum door.
 */
@Composable
fun CombinationLockView(
    correctCode: String,
    onUnlock: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val digits = remember { mutableStateListOf(0, 0, 0, 0) }
    var message by remember { mutableStateOf("Enter the four-digit code.") }
    var isWrongCode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E2126), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Drag handle indicator
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(5.dp)
                .background(Color.Gray.copy(alpha = 0.4f), CircleShape)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Museum Door Lock",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = if (isWrongCode) Color.Red else Color.LightGray,
                textAlign = TextAlign.Center
            )
        }

        // 4-digit mechanical dial row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            for (i in 0 until 4) {
                LockDial(
                    value = digits[i],
                    onIncrement = {
                        SoundManager.shared.play(GameSound.CLICK, 0.85f)
                        digits[i] = (digits[i] + 1) % 10 },
                    onDecrement = {
                        SoundManager.shared.play(GameSound.CLICK, 0.85f)
                        digits[i] = (digits[i] + 9) % 10 }
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    val enteredCode = digits.joinToString(separator = "")
                    if (enteredCode == correctCode) {
                        isWrongCode = false
                        message = "The lock clicks open."
                        onUnlock()
                    } else {
                        SoundManager.shared.play(GameSound.CURATOR_WRONG, 0.85f)
                        isWrongCode = true
                        message = "That combination does not work."
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Unlock")
            }
        }

        Text(
            text = "Hint: History holds the key.",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}