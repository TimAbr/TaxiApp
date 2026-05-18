package org.example.project.feature.location

import androidx.activity.ComponentActivity

interface LifecycleBinder {
    fun bind(activity: ComponentActivity)
    fun unbind()
}
