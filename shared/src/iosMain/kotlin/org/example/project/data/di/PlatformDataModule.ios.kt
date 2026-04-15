package org.example.project.data.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.example.project.data.feature.location.datasources.LocationDataSource
import org.example.project.data.feature.location.datasources.StubLocationDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
actual val platformDataModule: Module = module {
    single<Settings> {
        KeychainSettings(service = "org.example.project.tokens")
    }

    single<LocationDataSource> {
        StubLocationDataSource()
    }
}
