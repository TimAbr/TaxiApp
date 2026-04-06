package org.example.project.di

import org.example.project.data.feature.auth.datasources.auth.google.GoogleIdProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // TODO: Provide iOS GoogleIdProvider implementation when available
    // For now, if getId() is TODO, this won't be called yet
}
