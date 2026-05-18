package org.example.project.feature.location

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class PermissionLifecycleDelegate(
    private val requester: LocationPermissionRequester
) : LifecycleBinder, DefaultLifecycleObserver {

    private var _activity: ComponentActivity? = null
    val activity: ComponentActivity? get() = _activity

    override fun bind(activity: ComponentActivity) {
        _activity = activity
        activity.lifecycle.addObserver(this)
    }

    override fun unbind() {
        val activity = _activity ?: return
        requester.onUnbind(activity.isChangingConfigurations)
        activity.lifecycle.removeObserver(this)
        _activity = null
    }

    override fun onCreate(owner: LifecycleOwner) {
        val activity = owner as ComponentActivity
        _activity = activity
        requester.onBind(activity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        val activity = owner as ComponentActivity
        requester.onUnbind(activity.isChangingConfigurations)
        _activity = null
        owner.lifecycle.removeObserver(this)
    }
}
