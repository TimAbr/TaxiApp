package org.example.project.data.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.feature.auth.datasources.auth.remote.SERVER_PORT
import org.example.project.data.feature.auth.datasources.auth.remote.AuthRemoteDataSource
import org.example.project.data.feature.auth.datasources.auth.remote.AuthRemoteDataSourceImpl
import org.example.project.data.feature.auth.datasources.tokens.local.SettingsTokensDataSource
import org.example.project.data.feature.auth.datasources.tokens.local.TokensDataSource
import org.example.project.data.feature.auth.repositories.AuthRepositoryImpl
import org.example.project.data.feature.auth.repositories.TokenRepositoryImpl
import org.example.project.domain.feature.auth.repositories.AuthRepository
import org.example.project.domain.feature.auth.repositories.TokenRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    includes(platformDataModule)

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

    single (named("BASE_URL")){
        "http://127.0.0.1:$SERVER_PORT"
    }

    single {
        AuthRemoteDataSourceImpl(
            get(),
            get(named("BASE_URL"))
        )
    } bind AuthRemoteDataSource::class

    singleOf(::SettingsTokensDataSource) bind TokensDataSource::class
    singleOf(::TokenRepositoryImpl) bind TokenRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
