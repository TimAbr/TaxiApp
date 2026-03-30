package org.example.project.data.feature.auth.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class GoogleId(val value: String)
