package org.example.project.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformAppModule: Module = module {
    // Add Android-specific UI/App dependencies here
}
