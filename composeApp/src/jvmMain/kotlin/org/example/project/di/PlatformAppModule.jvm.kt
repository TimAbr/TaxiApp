package org.example.project.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformAppModule: Module = module {
    // Add JVM-specific UI/App dependencies here
}
