package org.example.project.di

import org.example.project.domain.feature.location.providers.PermissionManager
import org.example.project.feature.location.AndroidPermissionManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformAppModule: Module = module {
    single {
        AndroidPermissionManager()
    }

    single <PermissionManager> {
        get<AndroidPermissionManager>()
    }
}

