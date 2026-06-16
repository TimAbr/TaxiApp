package org.example.project.di

import org.example.project.data.di.dataModule
import org.example.project.domain.di.domainModule
import org.koin.dsl.module

val commonModule = module {
    includes(dataModule, domainModule)
}
