package com.peopleinnet.glcameraapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 70.dp)
    ) {
        FilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected
        )
    }
}