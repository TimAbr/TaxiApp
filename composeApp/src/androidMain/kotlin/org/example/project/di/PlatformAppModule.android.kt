package org.example.project.di

import org.example.project.domain.feature.location.providers.PermissionManager
import org.example.project.feature.location.AndroidPermissionManager
import org.example.project.feature.location.LocationPermissionRequester
import org.example.project.feature.location.PermissionLifecycleDelegate
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformAppModule: Module = module {
    single {
        LocationPermissionRequester()
    }

    single {
        PermissionLifecycleDelegate(get())
    }

    single {
        AndroidPermissionManager(get(), get(), get())
    }

    single <PermissionManager> {
        get<AndroidPermissionManager>()
    }
}
