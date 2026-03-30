package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

@OptIn(ExperimentalSettingsImplementation::class)
actual fun createSettings(): Settings {
    return KeychainSettings(service = "org.example.project.tokens")
}