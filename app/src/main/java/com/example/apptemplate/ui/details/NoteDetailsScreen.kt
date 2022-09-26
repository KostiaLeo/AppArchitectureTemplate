package com.example.apptemplate.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptemplate.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailsScreen(
    onNavigateUp: () -> Unit,
    viewModel: NoteDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    var textValue by remember {
        mutableStateOf(TextFieldValue(uiState.text))
    }
    LaunchedEffect(uiState.text) {
        textValue = textValue.copy(text = uiState.text)
    }
    val textFocusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NoteDetailsTopAppBar(uiState, onNavigateUp)
        },
        content = { paddingValues ->
            NoteDetailsContent(
                paddingValues = paddingValues,
                uiState = uiState,
                textValue = textValue,
                textFocusRequester = textFocusRequester,
                onTitleChanged = viewModel::onTitleChanged,
                onTextChanged = {
                    textValue = it
                    viewModel.onTextChanged(it.text)
                },
            )
        },
        floatingActionButton = {
            SaveNoteFAB(viewModel::saveNote)
        },
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                textFocusRequester.requestFocus()
                textValue = textValue.copy(selection = TextRange(index = textValue.text.length))
            },
    )

    NavigateUpOnNoteSaved(uiState, onNavigateUp)

    ShowSnackbarOnErrorMessage(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onErrorMessageShown = viewModel::onErrorMessageShown
    )
}

@Composable
private fun NoteDetailsTopAppBar(
    uiState: NoteDetailsUiState,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            val titleId = if (uiState.isNewNote) R.string.create_note else R.string.edit_note
            Text(text = stringResource(titleId))
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
private fun NoteDetailsContent(
    paddingValues: PaddingValues,
    uiState: NoteDetailsUiState,
    textValue: TextFieldValue,
    textFocusRequester: FocusRequester,
    onTitleChanged: (String) -> Unit,
    onTextChanged: (TextFieldValue) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

        TitleInput(uiState, onTitleChanged)

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

        TextInput(textFocusRequester, textValue, onTextChanged, uiState)
    }
}

@Composable
private fun TitleInput(
    uiState: NoteDetailsUiState,
    onTitleChanged: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val titleFocusRequester = remember { FocusRequester() }

    val fontSize = MaterialTheme.typography.h5.fontSize
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(titleFocusRequester),
        value = uiState.title,
        singleLine = true,
        textStyle = TextStyle(
            color = MaterialTheme.colors.onSurface,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        ),
        onValueChange = onTitleChanged,
        decorationBox = { innerTextField ->
            if (uiState.title.isEmpty()) {
                Text(
                    text = stringResource(R.string.type_title_hint),
                    color = Color.Gray,
                    fontSize = fontSize
                )
            }
            innerTextField()
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Next)
        })
    )

    LaunchedEffect(Unit) {
        if (uiState.isNewNote) {
            titleFocusRequester.requestFocus()
        }
    }
}

@Composable
private fun TextInput(
    textFocusRequester: FocusRequester,
    textValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    uiState: NoteDetailsUiState
) {
    val fontSize = MaterialTheme.typography.body1.fontSize
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(textFocusRequester),
        value = textValue,
        textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = fontSize),
        onValueChange = onTextChanged,
        decorationBox = { innerTextField ->
            if (uiState.text.isEmpty()) {
                Text(
                    text = stringResource(R.string.type_text_hint),
                    color = Color.Gray,
                    fontSize = fontSize
                )
            }
            innerTextField()
        }
    )
}

@Composable
private fun SaveNoteFAB(saveNote: () -> Unit) {
    FloatingActionButton(onClick = saveNote) {
        Icon(imageVector = Icons.Default.Done, stringResource(R.string.save_note))
    }
}

@Composable
private fun NavigateUpOnNoteSaved(
    uiState: NoteDetailsUiState,
    onNavigateUp: () -> Unit
) {
    LaunchedEffect(uiState) {
        if (uiState.isNoteSaved) {
            onNavigateUp()
        }
    }
}

@Composable
fun ShowSnackbarOnErrorMessage(
    scaffoldState: ScaffoldState,
    uiState: NoteDetailsUiState,
    onErrorMessageShown: () -> Unit
) {
    uiState.errorMessage?.let { errorMessage ->
        val messageText = stringResource(id = errorMessage)
        LaunchedEffect(scaffoldState, messageText, errorMessage, onErrorMessageShown) {
            scaffoldState.snackbarHostState.showSnackbar(messageText)
            onErrorMessageShown()
        }
    }
}