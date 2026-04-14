package org.example.project.domain.di

import org.example.project.domain.feature.auth.usecases.GetAuthStateUseCase
import org.example.project.domain.feature.auth.usecases.LoginWithGoogleUseCase
import org.example.project.domain.feature.auth.usecases.LogoutUseCase
import org.example.project.domain.feature.auth.usecases.ObserveAuthStateUseCase
import org.example.project.domain.feature.location.usecases.CheckLocationPermissionUseCase
import org.example.project.domain.feature.location.usecases.GetCurrentLocationUseCase
import org.example.project.domain.feature.location.usecases.ObserveLocationUpdatesUseCase
import org.example.project.domain.feature.location.usecases.RequestLocationPermissionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::LoginWithGoogleUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveAuthStateUseCase)
    factoryOf(::GetAuthStateUseCase)

    factoryOf(::CheckLocationPermissionUseCase)
    factoryOf(::RequestLocationPermissionUseCase)
    factoryOf(::GetCurrentLocationUseCase)
    factoryOf(::ObserveLocationUpdatesUseCase)
}
