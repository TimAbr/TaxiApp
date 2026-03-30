package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun createSettings(): Settings {
    return PreferencesSettings(Preferences.userNodeForPackage(SettingsTokensDataSource::class.java))
}