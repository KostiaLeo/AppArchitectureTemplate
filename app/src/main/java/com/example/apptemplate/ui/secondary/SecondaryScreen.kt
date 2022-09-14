package com.example.apptemplate.ui.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SecondaryScreen(
    param: String?,
    onNavigateToScreen1: () -> Unit,
    viewModel: SecondaryViewModel = hiltViewModel()
) {
    Text(
        text = "SECONDARY SCREEN with param $param",
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
            .clickable(onClick = onNavigateToScreen1),
        textAlign = TextAlign.Center,
        fontSize = 25.sp
    )
}