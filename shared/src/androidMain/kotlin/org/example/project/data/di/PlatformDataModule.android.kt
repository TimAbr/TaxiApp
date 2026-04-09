package org.example.project.data.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.example.project.data.feature.auth.datasources.auth.google.AndroidGoogleIdProvider
import org.example.project.data.feature.auth.datasources.auth.google.GoogleIdProvider
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { AndroidGoogleIdProvider(get()) } bind GoogleIdProvider::class

    single<Settings> {
        val context: Context = get()
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val sharedPrefs = EncryptedSharedPreferences.create(
            context,
            "encrypted_tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        SharedPreferencesSettings(sharedPrefs)
    }
}
