package com.example.apptemplate.semantics

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

val isPinnedKey = SemanticsPropertyKey<Boolean>("isPinned")
var SemanticsPropertyReceiver.isPinned by isPinnedKey