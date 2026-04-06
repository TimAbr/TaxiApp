package org.example.project.di

import org.example.project.data.feature.auth.datasources.auth.google.AndroidGoogleIdProvider
import org.example.project.data.feature.auth.datasources.auth.google.GoogleIdProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { AndroidGoogleIdProvider(get()) } bind GoogleIdProvider::class
}
