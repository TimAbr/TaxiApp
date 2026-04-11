package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.presentation.navigation.NavGraph
import org.example.project.presentation.theme.TaxiAppTheme
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {

    TaxiAppTheme {
        NavGraph()
    }

}
