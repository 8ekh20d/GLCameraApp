package com.peopleinnet.glcameraapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MetricsToggleButton(
    showMetrics: Boolean,
    onToggle: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(top = 32.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (showMetrics)
                    Icons.Default.Info
                else
                    Icons.Outlined.Info,
                contentDescription = "Toggle Metrics",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}