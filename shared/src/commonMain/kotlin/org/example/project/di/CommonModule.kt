package org.example.project.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.feature.auth.datasources.auth.remote.AuthRemoteDataSource
import org.example.project.data.feature.auth.datasources.auth.remote.AuthRemoteDataSourceImpl
import org.example.project.data.feature.auth.datasources.tokens.local.SettingsTokensDataSource
import org.example.project.data.feature.auth.datasources.tokens.local.TokensDataSource
import org.example.project.data.feature.auth.datasources.tokens.local.createSettings
import org.example.project.data.feature.auth.repositories.AuthRepositoryImpl
import org.example.project.data.feature.auth.repositories.TokenRepositoryImpl
import org.example.project.domain.feature.auth.repositories.AuthRepository
import org.example.project.domain.feature.auth.repositories.TokenRepository
import org.example.project.domain.feature.auth.usecases.GetAuthStateUseCase
import org.example.project.domain.feature.auth.usecases.LoginWithGoogleUseCase
import org.example.project.domain.feature.auth.usecases.ObserveAuthStateUseCase
import org.example.project.domain.feature.auth.usecases.LogoutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    single { createSettings() }
    
    singleOf(::SettingsTokensDataSource) bind TokensDataSource::class
    singleOf(::AuthRemoteDataSourceImpl) bind AuthRemoteDataSource::class
    singleOf(::TokenRepositoryImpl) bind TokenRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    factoryOf(::LoginWithGoogleUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveAuthStateUseCase)
    factoryOf(::GetAuthStateUseCase)
}
