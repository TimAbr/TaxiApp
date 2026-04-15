package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.feature.location.AndroidPermissionManager
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val permissionManager: AndroidPermissionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionManager.bind(this)

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionManager.unbind()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
