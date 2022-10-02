package com.example.apptemplate.utils

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.junit.rules.TestRule

fun <R : TestRule, A : ComponentActivity> AndroidComposeTestRule<R, A>.findTextField(text: Int): SemanticsNodeInteraction {
    return onNode(
        hasSetTextAction() and hasText(activity.getString(text))
    )
}