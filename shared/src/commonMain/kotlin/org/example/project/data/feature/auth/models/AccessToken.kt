package org.example.project.data.feature.auth.models

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class AccessToken(val value: String)
