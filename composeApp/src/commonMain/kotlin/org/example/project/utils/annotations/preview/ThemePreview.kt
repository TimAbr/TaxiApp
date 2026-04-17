package org.example.project.utils.annotations.preview

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    uiMode = 32,
    showBackground = true
)
annotation class ThemePreviews