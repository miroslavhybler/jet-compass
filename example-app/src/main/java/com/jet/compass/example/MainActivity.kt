package com.jet.compass.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jet.compass.Compass
import com.jet.compass.CompassAvailability
import com.jet.compass.CompassColors
import com.jet.compass.CompassDefaults
import com.jet.compass.CompassDimensions
import com.jet.compass.CompassState
import com.jet.compass.CompassTickDensity
import com.jet.compass.rememberCompassState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetCompassTheme {
                Scaffold { innerPadding ->
                    ExampleCompass(
                        modifier = Modifier.padding(paddingValues = innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExampleCompass(
    modifier: Modifier = Modifier,
    compassState: CompassState = rememberCompassState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state= rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Jet Compass",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )

        Compass(
            modifier = Modifier.size(276.dp),
            state = compassState,
            highlightDirectionDegrees = 60f,
        )

        compassSamples().forEach { sample ->
            CompassSampleCard(
                sample = sample,
                state = compassState,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleCompassPreview() {
    JetCompassTheme {
        ExampleCompass(
            compassState = rememberCompassState(
                initialHeadingDegrees = 35f,
                initialAvailability = CompassAvailability.Available,
            ),
        )
    }
}

@Composable
private fun CompassSampleCard(
    sample: CompassSample,
    state: CompassState,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 360.dp)
            .heightIn(min = 216.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = sample.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Compass(
                modifier = Modifier.size(sample.size),
                state = state,
                observeSensors = false,
                colors = sample.colors,
                dimensions = sample.dimensions,
                tickDensity = sample.tickDensity,
                highlightDirectionDegrees = sample.highlightDirectionDegrees,
                minSize = sample.size,
            )
        }
    }
}

@Composable
private fun compassSamples(): List<CompassSample> {
    val colorScheme = MaterialTheme.colorScheme
    val compactDimensions = CompassDefaults.dimensions(
        contentPadding = 12.dp,
        minorTickLength = 5.dp,
        majorTickLength = 11.dp,
        cardinalLabelInset = 30.dp,
        cardinalLabelSize = 16.sp,
        needleWidth = 13.dp,
        needleLengthRatio = 0.42f,
        needleTailLengthRatio = 0.24f,
    )
    val airyDimensions = compactDimensions.copy(
        contentPadding = 8.dp,
        cardinalLabelInset = 24.dp,
        needleLengthRatio = 0.36f,
        needleTailLengthRatio = 0.2f,
    )

    return listOf(
        CompassSample(
            label = "Tonal",
            colors = CompassDefaults.colors(
                containerColor = colorScheme.primaryContainer,
                contentColor = colorScheme.onPrimaryContainer,
                tickColor = colorScheme.onPrimaryContainer.copy(alpha = 0.55f),
                cardinalColor = colorScheme.onPrimaryContainer,
                primaryNeedleColor = colorScheme.error,
                secondaryNeedleColor = colorScheme.secondary,
                centerColor = colorScheme.error,
                highlightColor = colorScheme.tertiary,
                unsupportedContainerColor = colorScheme.primaryContainer,
                unsupportedContentColor = colorScheme.onPrimaryContainer,
            ),
            dimensions = compactDimensions,
            tickDensity = CompassTickDensity.Medium,
            highlightDirectionDegrees = 45f,
        ),
        CompassSample(
            label = "Minimal",
            colors = CompassDefaults.colors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface,
                tickColor = colorScheme.outlineVariant,
                cardinalColor = colorScheme.onSurfaceVariant,
                primaryNeedleColor = colorScheme.onSurface,
                secondaryNeedleColor = colorScheme.outline,
                centerColor = colorScheme.onSurface,
                highlightColor = colorScheme.primary,
            ),
            dimensions = airyDimensions,
            tickDensity = CompassTickDensity(
                minorTickDegrees = 45,
                majorTickDegrees = 90,
            ),
            highlightDirectionDegrees = 315f,
        ),
        CompassSample(
            label = "Contrast",
            colors = CompassDefaults.colors(
                containerColor = colorScheme.inverseSurface,
                contentColor = colorScheme.inverseOnSurface,
                tickColor = colorScheme.inverseOnSurface.copy(alpha = 0.58f),
                cardinalColor = colorScheme.inverseOnSurface,
                primaryNeedleColor = colorScheme.tertiaryContainer,
                secondaryNeedleColor = colorScheme.primaryContainer,
                centerColor = colorScheme.tertiaryContainer,
                highlightColor = colorScheme.error,
                outlineColor = colorScheme.outline,
                unsupportedContainerColor = colorScheme.inverseSurface,
                unsupportedContentColor = colorScheme.inverseOnSurface,
            ),
            dimensions = compactDimensions,
            tickDensity = CompassTickDensity.Dense,
            highlightDirectionDegrees = 135f,
        ),
        CompassSample(
            label = "Soft",
            colors = CompassDefaults.colors(
                containerColor = colorScheme.secondaryContainer,
                contentColor = colorScheme.onSecondaryContainer,
                tickColor = colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                cardinalColor = colorScheme.onSecondaryContainer,
                primaryNeedleColor = colorScheme.primary,
                secondaryNeedleColor = colorScheme.tertiary,
                centerColor = colorScheme.primary,
                highlightColor = colorScheme.error,
                unsupportedContainerColor = colorScheme.secondaryContainer,
                unsupportedContentColor = colorScheme.onSecondaryContainer,
            ),
            dimensions = compactDimensions.copy(
                contentPadding = 10.dp,
                needleLengthRatio = 0.38f,
                needleTailLengthRatio = 0.22f,
            ),
            tickDensity = CompassTickDensity.Sparse,
            highlightDirectionDegrees = 225f,
        ),
    )
}

private data class CompassSample(
    val label: String,
    val colors: CompassColors,
    val dimensions: CompassDimensions,
    val tickDensity: CompassTickDensity,
    val highlightDirectionDegrees: Float?,
    val size: Dp = 148.dp,
)
