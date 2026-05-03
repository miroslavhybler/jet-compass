package com.jet.compass

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.jet.compass.internal.CompassSensorEffect
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private data class CardinalPoint(
    val label: String,
    val degrees: Float,
)

private val CardinalPoints = listOf(
    CardinalPoint(label = "N", degrees = 0f),
    CardinalPoint(label = "E", degrees = 90f),
    CardinalPoint(label = "S", degrees = 180f),
    CardinalPoint(label = "W", degrees = 270f),
)

private const val FULL_CIRCLE_DEGREES = 360
private const val NORTH_OFFSET_DEGREES = 90f
private const val TICK_INTERVAL_DEGREES = 5
private const val TICK_COUNT = FULL_CIRCLE_DEGREES / TICK_INTERVAL_DEGREES
private const val MAJOR_TICK_INTERVAL_DEGREES = 30


@Composable
fun Compass(
    modifier: Modifier = Modifier,
    state: CompassState = rememberCompassState(),
    type: CompassType = CompassType.Circular,
    colors: CompassColors = CompassDefaults.colors(),
    minSize: Dp = CompassDefaults.MinSize,
    showHeading: Boolean = true,
    notSupportedContent: @Composable BoxScope.() -> Unit = {
        CompassDefaults.NotSupportedContent(colors)
    },
) {
    if (!LocalInspectionMode.current) {
        CompassSensorEffect(state = state)
    }

    CompassImpl(
        modifier = modifier,
        state = state,
        type = type,
        colors = colors,
        minSize = minSize,
        showHeading = showHeading,
        notSupportedContent = notSupportedContent,
    )
}

@Composable
private fun CompassImpl(
    modifier: Modifier,
    state: CompassState,
    type: CompassType,
    colors: CompassColors,
    minSize: Dp,
    showHeading: Boolean,
    notSupportedContent: @Composable BoxScope.() -> Unit,
) {
    if (state.availability == CompassAvailability.NotSupported) {
        NotSupportedCompass(
            modifier = modifier,
            colors = colors,
            minSize = minSize,
            content = notSupportedContent,
        )
        return
    }

    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .aspectRatio(ratio = 1f),
        shape = CircleShape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = BorderStroke(
            width = CompassDefaults.OutlineStrokeWidth,
            color = colors.outlineColor,
        ),
    ) {
        when (type) {
            CompassType.Circular -> {
                CircularCompass(
                    headingDegrees = state.headingDegrees,
                    colors = colors,
                    showHeading = showHeading,
                )
            }
        }
    }
}

@Composable
private fun CircularCompass(
    headingDegrees: Float,
    colors: CompassColors,
    showHeading: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = CompassDefaults.ContentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircularCompass(
                headingDegrees = headingDegrees,
                colors = colors,
            )
        }

        if (showHeading) {
            Text(
                text = headingDegrees.toHeadingText(),
                color = colors.contentColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun NotSupportedCompass(
    modifier: Modifier,
    colors: CompassColors,
    minSize: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .aspectRatio(ratio = 1f),
        shape = CircleShape,
        color = colors.unsupportedContainerColor,
        contentColor = colors.unsupportedContentColor,
        border = BorderStroke(
            width = CompassDefaults.OutlineStrokeWidth,
            color = colors.outlineColor
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

private fun DrawScope.drawCircularCompass(
    headingDegrees: Float,
    colors: CompassColors,
) {
    val radius = size.minDimension / 2f
    val outlineStroke = CompassDefaults.OutlineStrokeWidth.toPx()

    drawCircle(
        color = colors.outlineColor,
        radius = radius - outlineStroke / 2f,
        center = center,
        style = Stroke(width = outlineStroke),
    )
    drawTicks(
        headingDegrees = headingDegrees,
        colors = colors,
    )
    drawCardinalLabels(
        headingDegrees = headingDegrees,
        colors = colors,
    )
    drawNeedle(colors = colors)
}

private fun DrawScope.drawTicks(
    headingDegrees: Float,
    colors: CompassColors,
) {
    val radius = size.minDimension / 2f
    val minorTickLength = CompassDefaults.MinorTickLength.toPx()
    val majorTickLength = CompassDefaults.MajorTickLength.toPx()
    val minorStrokeWidth = CompassDefaults.MinorTickStrokeWidth.toPx()
    val majorStrokeWidth = CompassDefaults.MajorTickStrokeWidth.toPx()

    rotate(degrees = -headingDegrees, pivot = center) {
        for (tick in 0 until TICK_COUNT) {
            val degrees = tick * TICK_INTERVAL_DEGREES
            val isMajor = degrees % MAJOR_TICK_INTERVAL_DEGREES == 0
            val tickLength = if (isMajor) majorTickLength else minorTickLength
            val strokeWidth = if (isMajor) majorStrokeWidth else minorStrokeWidth
            val start = center.pointAt(radius - tickLength, degrees.toFloat())
            val end = center.pointAt(radius, degrees.toFloat())

            drawLine(
                color = colors.tickColor,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun DrawScope.drawCardinalLabels(
    headingDegrees: Float,
    colors: CompassColors,
) {
    val labelRadius = size.minDimension / 2f - CompassDefaults.CardinalLabelInset.toPx()
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colors.cardinalColor.toArgb()
        textAlign = Paint.Align.CENTER
        textSize = CompassDefaults.CardinalLabelSize.toPx()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    drawIntoCanvas { canvas ->
        CardinalPoints.fastForEach { cardinalPoint ->
            val position = center.pointAt(
                radius = labelRadius,
                degrees = cardinalPoint.degrees - headingDegrees,
            )
            val centeredBaseline = position.y - (textPaint.descent() + textPaint.ascent()) / 2f

            canvas.nativeCanvas.drawText(
                cardinalPoint.label,
                position.x,
                centeredBaseline,
                textPaint,
            )
        }
    }
}

private fun DrawScope.drawNeedle(colors: CompassColors) {
    val radius = size.minDimension / 2f
    val topLength = radius * 0.58f
    val tailLength = radius * 0.36f
    val halfNeedleWidth = CompassDefaults.NeedleWidth.toPx() / 2f
    val centerPinRadius = CompassDefaults.NeedleWidth.toPx() * 0.65f

    val northNeedle = Path().apply {
        moveTo(center.x, center.y - topLength)
        lineTo(center.x - halfNeedleWidth, center.y + centerPinRadius)
        lineTo(center.x + halfNeedleWidth, center.y + centerPinRadius)
        close()
    }
    val southNeedle = Path().apply {
        moveTo(center.x, center.y + tailLength)
        lineTo(center.x - halfNeedleWidth * 0.65f, center.y - centerPinRadius)
        lineTo(center.x + halfNeedleWidth * 0.65f, center.y - centerPinRadius)
        close()
    }

    drawPath(path = southNeedle, color = colors.secondaryNeedleColor)
    drawPath(path = northNeedle, color = colors.primaryNeedleColor)
    drawCircle(color = colors.centerColor, radius = centerPinRadius, center = center)
    drawCircle(
        color = colors.containerColor,
        radius = centerPinRadius * 0.42f,
        center = center,
    )
}

private fun Offset.pointAt(radius: Float, degrees: Float): Offset {
    val radians = Math.toRadians((degrees - NORTH_OFFSET_DEGREES).toDouble())
    return Offset(
        x = x + radius * cos(radians).toFloat(),
        y = y + radius * sin(radians).toFloat(),
    )
}

private fun Float.toHeadingText(): String {
    val degrees = roundToInt().floorMod(FULL_CIRCLE_DEGREES)
    return "$degrees\u00B0"
}

private fun Int.floorMod(modulus: Int): Int = ((this % modulus) + modulus) % modulus


@Composable
@PreviewLightDark
private fun CompassPreview() {
    MaterialTheme() {
        Compass()
    }
}