package org.example.project.di

import org.example.project.presentation.auth.AuthViewModel
import org.example.project.presentation.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(platformAppModule)
    viewModelOf(::AuthViewModel)
    viewModelOf(::MainViewModel)
}
