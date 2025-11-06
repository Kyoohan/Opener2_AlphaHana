package com.kyoohan.opener2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyoohan.opener2.data.ChatSession
import com.kyoohan.opener2.ui.theme.*

@Composable
fun TabBar(
    sessions: List<ChatSession>,
    activeSessionId: String?,
    onTabSelect: (String) -> Unit,
    onNewTab: () -> Unit,
    onCloseTab: (String) -> Unit,
    highContrastMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val tabBarBg = if (highContrastMode) HighContrastBackgroundColor else Color(0xFF2E2E2E)
    val iconColor = if (highContrastMode) HighContrastTextColor else Color.White
    
    Row(
        modifier = modifier
            .background(tabBarBg)
            .then(
                if (highContrastMode) {
                    Modifier.border(
                        width = 2.dp,
                        color = HighContrastBorderColor
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sessions) { session ->
                TabItem(
                    session = session,
                    isActive = session.id == activeSessionId,
                    highContrastMode = highContrastMode,
                    onSelect = { onTabSelect(session.id) },
                    onClose = { onCloseTab(session.id) }
                )
            }
        }
        
        IconButton(
            onClick = onNewTab,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Chat",
                tint = iconColor
            )
        }
    }
}

@Composable
private fun TabItem(
    session: ChatSession,
    isActive: Boolean,
    highContrastMode: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val backgroundColor = if (highContrastMode) {
        if (isActive) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)
    } else {
        if (isActive) Color(0xFF4A4A4A) else Color(0xFF3A3A3A)
    }
    
    val contentColor = if (highContrastMode) {
        HighContrastTextColor
    } else {
        if (isActive) Color.White else Color(0xFFB0B0B0)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (highContrastMode) {
                    Modifier.border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = HighContrastBorderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .widthIn(min = 80.dp, max = 150.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = session.getDisplayTitle(),
                color = contentColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (highContrastMode && isActive) FontWeight.Bold else FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}