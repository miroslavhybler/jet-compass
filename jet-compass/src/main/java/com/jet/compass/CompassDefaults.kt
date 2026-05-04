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
    val HighlightStrokeWidth: Dp = 3.dp
    const val NeedleLengthRatio: Float = 0.58f
    const val NeedleTailLengthRatio: Float = 0.36f
    const val CenterPinRadiusRatio: Float = 0.65f
    const val HighlightStartRadiusRatio: Float = 0.18f
    const val HighlightEndRadiusRatio: Float = 0.92f
    const val AutoScaleDimensions: Boolean = true
    const val MinimumDimensionScale: Float = 0.55f
    const val MaximumDimensionScale: Float = 1f

    val TickDensity: CompassTickDensity = CompassTickDensity()

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
        highlightColor: Color = MaterialTheme.colorScheme.tertiary,
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
        highlightColor = highlightColor,
        outlineColor = outlineColor,
        unsupportedContainerColor = unsupportedContainerColor,
        unsupportedContentColor = unsupportedContentColor,
    )

    fun dimensions(
        contentPadding: Dp = ContentPadding,
        outlineStrokeWidth: Dp = OutlineStrokeWidth,
        minorTickStrokeWidth: Dp = MinorTickStrokeWidth,
        majorTickStrokeWidth: Dp = MajorTickStrokeWidth,
        minorTickLength: Dp = MinorTickLength,
        majorTickLength: Dp = MajorTickLength,
        cardinalLabelInset: Dp = CardinalLabelInset,
        cardinalLabelSize: TextUnit = CardinalLabelSize,
        needleWidth: Dp = NeedleWidth,
        highlightStrokeWidth: Dp = HighlightStrokeWidth,
        needleLengthRatio: Float = NeedleLengthRatio,
        needleTailLengthRatio: Float = NeedleTailLengthRatio,
        centerPinRadiusRatio: Float = CenterPinRadiusRatio,
        highlightStartRadiusRatio: Float = HighlightStartRadiusRatio,
        highlightEndRadiusRatio: Float = HighlightEndRadiusRatio,
        autoScale: Boolean = AutoScaleDimensions,
        scaleBaselineSize: Dp = MinSize,
        minimumScale: Float = MinimumDimensionScale,
        maximumScale: Float = MaximumDimensionScale,
    ): CompassDimensions = CompassDimensions(
        contentPadding = contentPadding,
        outlineStrokeWidth = outlineStrokeWidth,
        minorTickStrokeWidth = minorTickStrokeWidth,
        majorTickStrokeWidth = majorTickStrokeWidth,
        minorTickLength = minorTickLength,
        majorTickLength = majorTickLength,
        cardinalLabelInset = cardinalLabelInset,
        cardinalLabelSize = cardinalLabelSize,
        needleWidth = needleWidth,
        highlightStrokeWidth = highlightStrokeWidth,
        needleLengthRatio = needleLengthRatio,
        needleTailLengthRatio = needleTailLengthRatio,
        centerPinRadiusRatio = centerPinRadiusRatio,
        highlightStartRadiusRatio = highlightStartRadiusRatio,
        highlightEndRadiusRatio = highlightEndRadiusRatio,
        autoScale = autoScale,
        scaleBaselineSize = scaleBaselineSize,
        minimumScale = minimumScale,
        maximumScale = maximumScale,
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
    val highlightColor: Color,
    val outlineColor: Color,
    val unsupportedContainerColor: Color,
    val unsupportedContentColor: Color,
)

@Immutable
data class CompassDimensions(
    val contentPadding: Dp,
    val outlineStrokeWidth: Dp,
    val minorTickStrokeWidth: Dp,
    val majorTickStrokeWidth: Dp,
    val minorTickLength: Dp,
    val majorTickLength: Dp,
    val cardinalLabelInset: Dp,
    val cardinalLabelSize: TextUnit,
    val needleWidth: Dp,
    val highlightStrokeWidth: Dp,
    val needleLengthRatio: Float,
    val needleTailLengthRatio: Float,
    val centerPinRadiusRatio: Float,
    val highlightStartRadiusRatio: Float,
    val highlightEndRadiusRatio: Float,
    val autoScale: Boolean,
    val scaleBaselineSize: Dp,
    val minimumScale: Float,
    val maximumScale: Float,
) {
    init {
        require(needleLengthRatio > 0f) { "needleLengthRatio must be greater than 0." }
        require(needleTailLengthRatio > 0f) { "needleTailLengthRatio must be greater than 0." }
        require(centerPinRadiusRatio > 0f) { "centerPinRadiusRatio must be greater than 0." }
        require(highlightStartRadiusRatio >= 0f) {
            "highlightStartRadiusRatio must be greater than or equal to 0."
        }
        require(highlightEndRadiusRatio > highlightStartRadiusRatio) {
            "highlightEndRadiusRatio must be greater than highlightStartRadiusRatio."
        }
        require(highlightEndRadiusRatio <= 1f) {
            "highlightEndRadiusRatio must be less than or equal to 1."
        }
        require(scaleBaselineSize > 0.dp) { "scaleBaselineSize must be greater than 0.dp." }
        require(minimumScale > 0f) { "minimumScale must be greater than 0." }
        require(maximumScale >= minimumScale) {
            "maximumScale must be greater than or equal to minimumScale."
        }
    }
}

@Immutable
data class CompassTickDensity(
    val minorTickDegrees: Int = 5,
    val majorTickDegrees: Int = 30,
) {
    init {
        require(minorTickDegrees in 1..90) { "minorTickDegrees must be between 1 and 90." }
        require(majorTickDegrees in minorTickDegrees..180) {
            "majorTickDegrees must be between minorTickDegrees and 180."
        }
        require(FULL_CIRCLE_DEGREES % minorTickDegrees == 0) {
            "minorTickDegrees must divide evenly into 360."
        }
        require(FULL_CIRCLE_DEGREES % majorTickDegrees == 0) {
            "majorTickDegrees must divide evenly into 360."
        }
    }

    companion object {
        val Dense: CompassTickDensity = CompassTickDensity(
            minorTickDegrees = 5,
            majorTickDegrees = 30,
        )
        val Medium: CompassTickDensity = CompassTickDensity(
            minorTickDegrees = 10,
            majorTickDegrees = 30,
        )
        val Sparse: CompassTickDensity = CompassTickDensity(
            minorTickDegrees = 15,
            majorTickDegrees = 45,
        )
    }
}

private const val FULL_CIRCLE_DEGREES = 360
