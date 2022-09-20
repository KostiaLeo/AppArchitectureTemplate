package com.example.apptemplate.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailsScreen(
    onNavigateUp: () -> Unit,
    viewModel: NoteDetailsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState.isNoteSaved) {
            onNavigateUp()
        }
    }

    val textFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                textFocusRequester.requestFocus()
            },
        topBar = {

        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = uiState.title,
                    singleLine = true,
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 24.sp),
                    onValueChange = viewModel::onTitleChanged,
                    decorationBox = { innerTextField ->
                        if (uiState.title.isEmpty()) {
                            Text(text = "Type title", color = Color.Gray, fontSize = 24.sp)
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFocusRequester),
                    value = uiState.text,
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 16.sp),
                    onValueChange = viewModel::onTextChanged,
                    decorationBox = { innerTextField ->
                        if (uiState.text.isEmpty()) {
                            Text(text = "Type text", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                )
            }
        },
        snackbarHost = {},
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::saveNote) {
                Icon(imageVector = Icons.Default.Done, contentDescription = null)
            }
        }
    )
}