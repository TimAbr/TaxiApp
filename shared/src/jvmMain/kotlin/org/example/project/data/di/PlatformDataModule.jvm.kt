package org.example.project.data.di

import TaxiApp.shared.DesktopConfig
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.example.project.data.feature.auth.datasources.auth.google.DesktopGoogleIdProvider
import org.example.project.data.feature.auth.datasources.auth.google.GoogleIdProvider
import org.example.project.data.feature.auth.datasources.auth.remote.SERVER_PORT
import org.example.project.data.feature.auth.datasources.tokens.local.SettingsTokensDataSource
import org.example.project.data.feature.location.datasources.LocationDataSource
import org.example.project.data.feature.location.datasources.StubLocationDataSource
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformDataModule: Module = module {
    single<Settings> {
        PreferencesSettings(
            Preferences.userNodeForPackage(SettingsTokensDataSource::class.java),
        )
    }
    single<GoogleIdProvider> {
        DesktopGoogleIdProvider(
            desktopClientId = DesktopConfig.GOOGLE_DESKTOP_ID,
            desktopClientSecret = DesktopConfig.GOOGLE_DESKTOP_SECRET,
        )
    }

    single(named("BASE_URL")) {
        "http://127.0.0.1:$SERVER_PORT"
    }

    single<LocationDataSource> {
        StubLocationDataSource()
    }
}
