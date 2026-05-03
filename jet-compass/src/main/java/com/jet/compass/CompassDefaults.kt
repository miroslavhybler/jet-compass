package com.jet.compass

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CompassDefaults {
    val MinSize: Dp = 240.dp
    val ContentPadding: Dp = 20.dp
    val OutlineStrokeWidth: Dp = 1.dp
    val MinorTickStrokeWidth: Dp = 1.dp
    val MajorTickStrokeWidth: Dp = 2.dp
    val MinorTickLength: Dp = 8.dp
    val MajorTickLength: Dp = 16.dp
    val CardinalLabelInset: Dp = 40.dp
    val CardinalLabelSize: TextUnit = 18.sp
    val NeedleWidth: Dp = 16.dp

    const val NotSupportedText: String = "Not supported"

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        tickColor: Color = MaterialTheme.colorScheme.outline,
        cardinalColor: Color = MaterialTheme.colorScheme.onSurface,
        primaryNeedleColor: Color = MaterialTheme.colorScheme.primary,
        secondaryNeedleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        centerColor: Color = MaterialTheme.colorScheme.primary,
        outlineColor: Color = MaterialTheme.colorScheme.outlineVariant,
        unsupportedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        unsupportedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): CompassColors = CompassColors(
        containerColor = containerColor,
        contentColor = contentColor,
        tickColor = tickColor,
        cardinalColor = cardinalColor,
        primaryNeedleColor = primaryNeedleColor,
        secondaryNeedleColor = secondaryNeedleColor,
        centerColor = centerColor,
        outlineColor = outlineColor,
        unsupportedContainerColor = unsupportedContainerColor,
        unsupportedContentColor = unsupportedContentColor,
    )

    @Composable
    fun NotSupportedContent(colors: CompassColors = colors()) {
        Text(
            text = NotSupportedText,
            color = colors.unsupportedContentColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Immutable
data class CompassColors constructor(
    val containerColor: Color,
    val contentColor: Color,
    val tickColor: Color,
    val cardinalColor: Color,
    val primaryNeedleColor: Color,
    val secondaryNeedleColor: Color,
    val centerColor: Color,
    val outlineColor: Color,
    val unsupportedContainerColor: Color,
    val unsupportedContentColor: Color,
)
