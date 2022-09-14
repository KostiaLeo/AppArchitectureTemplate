package com.example.apptemplate.ui.main

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
fun MainScreen(
    param: String?,
    onNavigateToScreen2: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    Text(
        text = "MAIN SCREEN with param $param",
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable(onClick = onNavigateToScreen2),
        textAlign = TextAlign.Center,
        fontSize = 25.sp
    )
}