package org.example.project.data.feature.auth.models

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RefreshToken(val value: String)
