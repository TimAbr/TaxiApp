package org.example.project.di

import org.example.project.presentation.auth.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AuthViewModel)
}
