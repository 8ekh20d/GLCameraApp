package com.peopleinnet.glcameraapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterSelector(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Normal", "Gray", "Sepia")

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        filters.forEach { filter ->
            Text(
                text = filter.uppercase(),
                color = if (selectedFilter == filter)
                    Color.Companion.White
                else
                    Color.Companion.White.copy(alpha = 0.5f),
                fontWeight = if (selectedFilter == filter)
                    FontWeight.Companion.Bold
                else
                    FontWeight.Companion.Normal,
                fontSize = 14.sp,
                modifier = Modifier.Companion
                    .clickable {
                        onFilterSelected(filter)
                    }
            )
        }
    }
}