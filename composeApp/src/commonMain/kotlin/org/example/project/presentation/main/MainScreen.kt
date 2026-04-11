package org.example.project.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.main_screen_placeholder

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(Res.string.main_screen_placeholder))
    }
}
