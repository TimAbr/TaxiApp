package org.example.project.presentation.location.permission

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.presentation.theme.TaxiAppTheme
import org.example.project.utils.annotations.preview.ThemePreviews
import org.jetbrains.compose.resources.stringResource

private const val ANIMATION_START_DELAY = 300L

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
            LocationPermissionIllustration(permissionStatus = permissionStatus)

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(permissionStatus.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(permissionStatus.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
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
                    text = stringResource(permissionStatus.buttonTextRes),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun LocationPermissionIllustration(
    permissionStatus: PermissionStatus,
    modifier: Modifier = Modifier,
) {
    var isStarted by remember { mutableStateOf(false) }
    var showGrayPin by remember { mutableStateOf(false) }
    var showCross by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(ANIMATION_START_DELAY)
        isStarted = true
    }

    LaunchedEffect(isStarted, permissionStatus) {
        if (isStarted && permissionStatus == PermissionStatus.DENIED_ALWAYS) {
            delay(400)
            showGrayPin = true
            delay(300)
            showCross = true
        } else {
            showGrayPin = false
            showCross = false
        }
    }

    val springSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
    )

    val pinScale by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = springSpec,
    )

    val pinColor by animateColorAsState(
        targetValue = if (showGrayPin) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500)
    )

    val crossScale by animateFloatAsState(
        targetValue = if (showCross) 1f else 0f,
        animationSpec = springSpec,
    )

    val backgroundColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .size(200.dp)
            .drawBehind {
                drawCircle(color = backgroundColor)
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isStarted && !showGrayPin && permissionStatus != PermissionStatus.DENIED_ALWAYS) {
            PulseCircle(modifier = Modifier.size(120.dp))
        }

        LocationPin(
            color = pinColor,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    scaleX = pinScale
                    scaleY = pinScale
                }
        )

        DeniedCross(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = crossScale
                    scaleY = crossScale
                }
        )
    }
}

@Composable
private fun DeniedCross(
    modifier: Modifier = Modifier
) {
    val errorColor = MaterialTheme.colorScheme.error
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        drawLine(
            color = errorColor,
            start = Offset(size.width * 0.2f, size.height * 0.2f),
            end = Offset(size.width * 0.8f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = errorColor,
            start = Offset(size.width * 0.8f, size.height * 0.2f),
            end = Offset(size.width * 0.2f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun PulseCircle(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart,
        ),
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart,
        ),
    )

    val pulseColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    Box(
        modifier = modifier.drawBehind {
            drawCircle(
                color = pulseColor,
                radius = (size.minDimension / 2) * scale,
                alpha = alpha
            )
        }
    )
}

@Composable
private fun LocationPin(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(color = color)
                drawCircle(
                    color = Color.White,
                    radius = 9.dp.toPx()
                )
            }
    )
}

@ThemePreviews
@Composable
private fun LocationPermissionInitialPreview() {
    TaxiAppTheme {
        LocationPermissionContent(
            permissionStatus = PermissionStatus.NOT_DETERMINED,
            onGrantClick = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LocationPermissionDeniedPreview() {
    TaxiAppTheme {
        LocationPermissionContent(
            permissionStatus = PermissionStatus.DENIED,
            onGrantClick = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LocationPermissionDeniedAlwaysPreview() {
    TaxiAppTheme {
        LocationPermissionContent(
            permissionStatus = PermissionStatus.DENIED_ALWAYS,
            onGrantClick = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LocationPermissionLowAccuracyPreview() {
    TaxiAppTheme {
        LocationPermissionContent(
            permissionStatus = PermissionStatus.LOW_ACCURACY,
            onGrantClick = {},
        )
    }
}
