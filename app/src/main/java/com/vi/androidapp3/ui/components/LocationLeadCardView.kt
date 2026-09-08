package com.vi.androidapp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.androidapp3.data.LocationLead

/**
 * Card item representing an individual investigable trail lead in the museum selection list.
 * Dynamically adjusts styling based on lock status and completion progress.
 */
@Composable
fun LocationLeadCardView(
    lead: LocationLead,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = when {
        isCompleted -> Color.Green.copy(alpha = 0.13f)
        !isUnlocked -> Color.Black.copy(alpha = 0.25f)
        else -> Color.White.copy(alpha = 0.08f)
    }

    val borderColor = when {
        isCompleted -> Color.Green.copy(alpha = 0.45f)
        !isUnlocked -> Color.White.copy(alpha = 0.08f)
        else -> Color(0xFFFF9800).copy(alpha = 0.35f)
    }

    val iconBackground = when {
        isCompleted -> Color.Green.copy(alpha = 0.25f)
        !isUnlocked -> Color.Gray.copy(alpha = 0.25f)
        else -> Color(0xFFFF9800).copy(alpha = 0.25f)
    }

    val iconColor = when {
        isCompleted -> Color.Green
        !isUnlocked -> Color.White.copy(alpha = 0.45f)
        else -> Color(0xFFFF9800)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isUnlocked) 1.0f else 0.55f)
            .background(cardBackground, RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(enabled = isUnlocked) { onAction() }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isCompleted -> Icons.Default.Check
                        !isUnlocked -> Icons.Default.Lock
                        else -> Icons.Default.Place
                    },
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lead.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (!isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.55f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = lead.subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = if (isUnlocked) 0.75f else 0.45f)
                )

                Text(
                    text = lead.publicMystery,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = if (isUnlocked) 0.62f else 0.35f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}