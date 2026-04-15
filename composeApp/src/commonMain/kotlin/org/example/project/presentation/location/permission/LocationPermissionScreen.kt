package org.example.project.presentation.location.permission

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.presentation.theme.TaxiAppTheme
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.location_permission_button
import taxiapp.composeapp.generated.resources.location_permission_denied_always
import taxiapp.composeapp.generated.resources.location_permission_description
import taxiapp.composeapp.generated.resources.location_permission_settings_button
import taxiapp.composeapp.generated.resources.location_permission_title

@Composable
fun LocationPermissionScreen(
    viewModel: LocationPermissionViewModel,
    onNavigate: () -> Unit,
) {
    val permissionStatus by viewModel.permissionStatus.collectAsStateWithLifecycle()

    LaunchedEffect(permissionStatus) {
        if (permissionStatus == PermissionStatus.GRANTED) {
            onNavigate()
        }
    }

    LocationPermissionContent(
        permissionStatus = permissionStatus,
        onGrantClick = viewModel::requestPermission,
    )
}

@Composable
private fun LocationPermissionContent(
    permissionStatus: PermissionStatus,
    onGrantClick: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LocationPermissionIllustration()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(Res.string.location_permission_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (permissionStatus == PermissionStatus.DENIED_ALWAYS) {
                    stringResource(Res.string.location_permission_denied_always)
                } else {
                    stringResource(Res.string.location_permission_description)
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGrantClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = if (permissionStatus == PermissionStatus.DENIED_ALWAYS) {
                        stringResource(Res.string.location_permission_settings_button)
                    } else {
                        stringResource(Res.string.location_permission_button)
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun LocationPermissionIllustration(
    modifier: Modifier = Modifier,
) {
    var isStarted by remember {
        mutableStateOf(false)
    }

    val pinScale by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    val infiniteTransition = rememberInfiniteTransition()

    LaunchedEffect(Unit) {
        delay(300)
        isStarted = true
    }

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.Center,
        ) {

            if (isStarted) {
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000),
                        repeatMode = RepeatMode.Restart,
                    ),
                )

                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000),
                        repeatMode = RepeatMode.Restart,
                    ),
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = pulseAlpha
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            shape = CircleShape,
                        ),
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = pinScale
                        scaleY = pinScale
                    }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocationPermissionScreenPreview() {
    TaxiAppTheme {
        LocationPermissionContent(
            permissionStatus = PermissionStatus.DENIED,
            onGrantClick = {},
        )
    }
}
