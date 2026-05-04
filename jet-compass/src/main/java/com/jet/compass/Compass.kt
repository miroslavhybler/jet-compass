package com.jet.compass

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.jet.compass.internal.CompassSensorEffect
import kotlin.math.cos
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

private val CardinalTickDegrees = setOf(0, 90, 180, 270)

private const val FULL_CIRCLE_DEGREES = 360
private const val NORTH_OFFSET_DEGREES = 90f


@Composable
fun Compass(
    modifier: Modifier = Modifier,
    state: CompassState = rememberCompassState(),
    observeSensors: Boolean = true,
    type: CompassType = CompassType.Circular,
    colors: CompassColors = CompassDefaults.colors(),
    dimensions: CompassDimensions = CompassDefaults.dimensions(),
    tickDensity: CompassTickDensity = CompassDefaults.TickDensity,
    highlightDirectionDegrees: Float? = null,
    minSize: Dp = CompassDefaults.MinSize,
    notSupportedContent: @Composable BoxScope.() -> Unit = {
        CompassDefaults.NotSupportedContent(colors)
    },
) {
    if (observeSensors && !LocalInspectionMode.current) {
        CompassSensorEffect(state = state)
    }

    CompassImpl(
        modifier = modifier,
        state = state,
        type = type,
        colors = colors,
        dimensions = dimensions,
        tickDensity = tickDensity,
        highlightDirectionDegrees = highlightDirectionDegrees,
        minSize = minSize,
        notSupportedContent = notSupportedContent,
    )
}

@Composable
private fun CompassImpl(
    modifier: Modifier,
    state: CompassState,
    type: CompassType,
    colors: CompassColors,
    dimensions: CompassDimensions,
    tickDensity: CompassTickDensity,
    highlightDirectionDegrees: Float?,
    minSize: Dp,
    notSupportedContent: @Composable BoxScope.() -> Unit,
) {
    if (state.availability == CompassAvailability.NotSupported) {
        NotSupportedCompass(
            modifier = modifier,
            colors = colors,
            dimensions = dimensions,
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
            width = dimensions.outlineStrokeWidth,
            color = colors.outlineColor,
        ),
    ) {
        when (type) {
            CompassType.Circular -> {
                CircularCompass(
                    headingDegrees = state.headingDegrees,
                    colors = colors,
                    dimensions = dimensions,
                    tickDensity = tickDensity,
                    highlightDirectionDegrees = highlightDirectionDegrees,
                )
            }
        }
    }
}

@Composable
private fun CircularCompass(
    headingDegrees: Float,
    colors: CompassColors,
    dimensions: CompassDimensions,
    tickDensity: CompassTickDensity,
    highlightDirectionDegrees: Float?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val dimensionScale = dimensions.scaleFor(availableSize = minCompassDimension())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = dimensions.contentPadding.scaledBy(scale = dimensionScale)),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircularCompass(
                    headingDegrees = headingDegrees,
                    colors = colors,
                    dimensions = dimensions,
                    tickDensity = tickDensity,
                    highlightDirectionDegrees = highlightDirectionDegrees,
                    dimensionScale = dimensionScale,
                )
            }
        }
    }
}

@Composable
private fun NotSupportedCompass(
    modifier: Modifier,
    colors: CompassColors,
    dimensions: CompassDimensions,
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
            width = dimensions.outlineStrokeWidth,
            color = colors.outlineColor
        ),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val dimensionScale = dimensions.scaleFor(availableSize = minCompassDimension())

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = dimensions.contentPadding.scaledBy(scale = dimensionScale)),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

private fun DrawScope.drawCircularCompass(
    headingDegrees: Float,
    colors: CompassColors,
    dimensions: CompassDimensions,
    tickDensity: CompassTickDensity,
    highlightDirectionDegrees: Float?,
    dimensionScale: Float,
) {
    val radius = size.minDimension / 2f
    val outlineStroke = scaledPx(value = dimensions.outlineStrokeWidth, scale = dimensionScale)

    drawCircle(
        color = colors.outlineColor,
        radius = radius - outlineStroke / 2f,
        center = center,
        style = Stroke(width = outlineStroke),
    )
    drawTicks(
        headingDegrees = headingDegrees,
        colors = colors,
        dimensions = dimensions,
        tickDensity = tickDensity,
        dimensionScale = dimensionScale,
    )
    highlightDirectionDegrees?.let { directionDegrees ->
        drawHighlightDirection(
            headingDegrees = headingDegrees,
            directionDegrees = directionDegrees,
            colors = colors,
            dimensions = dimensions,
            dimensionScale = dimensionScale,
        )
    }
    drawCardinalLabels(
        headingDegrees = headingDegrees,
        colors = colors,
        dimensions = dimensions,
        dimensionScale = dimensionScale,
    )
    drawNeedle(
        colors = colors,
        dimensions = dimensions,
        dimensionScale = dimensionScale,
    )
}

private fun DrawScope.drawTicks(
    headingDegrees: Float,
    colors: CompassColors,
    dimensions: CompassDimensions,
    tickDensity: CompassTickDensity,
    dimensionScale: Float,
) {
    val radius = size.minDimension / 2f
    val minorTickLength = scaledPx(value = dimensions.minorTickLength, scale = dimensionScale)
    val majorTickLength = scaledPx(value = dimensions.majorTickLength, scale = dimensionScale)
    val minorStrokeWidth = scaledPx(value = dimensions.minorTickStrokeWidth, scale = dimensionScale)
    val majorStrokeWidth = scaledPx(value = dimensions.majorTickStrokeWidth, scale = dimensionScale)

    rotate(degrees = -headingDegrees, pivot = center) {
        tickDensity.tickDegrees().fastForEach { degrees ->
            val isMajor = degrees.isCardinalTick() || degrees % tickDensity.majorTickDegrees == 0
            val tickLength = if (isMajor) majorTickLength else minorTickLength
            val strokeWidth = if (isMajor) majorStrokeWidth else minorStrokeWidth
            val start = center.pointAt(radius = radius - tickLength, degrees = degrees.toFloat())
            val end = center.pointAt(radius = radius, degrees = degrees.toFloat())

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

private fun DrawScope.drawHighlightDirection(
    headingDegrees: Float,
    directionDegrees: Float,
    colors: CompassColors,
    dimensions: CompassDimensions,
    dimensionScale: Float,
) {
    val radius = size.minDimension / 2f
    val startRadius = radius * dimensions.highlightStartRadiusRatio
    val endRadius = radius * dimensions.highlightEndRadiusRatio
    val strokeWidth = scaledPx(
        value = dimensions.highlightStrokeWidth,
        scale = dimensionScale,
    )
    val normalizedDirection = normalizeCompassDegrees(directionDegrees)

    rotate(degrees = -headingDegrees, pivot = center) {
        drawLine(
            color = colors.highlightColor,
            start = center.pointAt(
                radius = startRadius,
                degrees = normalizedDirection,
            ),
            end = center.pointAt(
                radius = endRadius,
                degrees = normalizedDirection,
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawCardinalLabels(
    headingDegrees: Float,
    colors: CompassColors,
    dimensions: CompassDimensions,
    dimensionScale: Float,
) {
    val labelRadius = (size.minDimension / 2f - scaledPx(
        value = dimensions.cardinalLabelInset,
        scale = dimensionScale
    )
            ).coerceAtLeast(minimumValue = 0f)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colors.cardinalColor.toArgb()
        textAlign = Paint.Align.CENTER
        textSize = dimensions.cardinalLabelSize.toPx() * dimensionScale
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

private fun DrawScope.drawNeedle(
    colors: CompassColors,
    dimensions: CompassDimensions,
    dimensionScale: Float,
) {
    val radius = size.minDimension / 2f
    val topLength = radius * dimensions.needleLengthRatio
    val tailLength = radius * dimensions.needleTailLengthRatio
    val needleWidth = scaledPx(dimensions.needleWidth, dimensionScale)
    val halfNeedleWidth = needleWidth / 2f
    val centerPinRadius = needleWidth * dimensions.centerPinRadiusRatio

    val northNeedle = Path().apply {
        moveTo(x = center.x, y = center.y - topLength)
        lineTo(x = center.x - halfNeedleWidth, y = center.y + centerPinRadius)
        lineTo(x = center.x + halfNeedleWidth, y = center.y + centerPinRadius)
        close()
    }
    val southNeedle = Path().apply {
        moveTo(x = center.x, y = center.y + tailLength)
        lineTo(x = center.x - halfNeedleWidth * 0.65f, y = center.y - centerPinRadius)
        lineTo(x = center.x + halfNeedleWidth * 0.65f, y = center.y - centerPinRadius)
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

private fun CompassTickDensity.tickDegrees(): List<Int> {
    val degrees = sortedSetOf<Int>()
    for (degree in 0 until FULL_CIRCLE_DEGREES step minorTickDegrees) {
        degrees += degree
    }
    for (degree in 0 until FULL_CIRCLE_DEGREES step majorTickDegrees) {
        degrees += degree
    }
    CardinalTickDegrees.forEach { degrees += it }
    return degrees.toList()
}

private fun Int.isCardinalTick(): Boolean = this in CardinalTickDegrees

private fun CompassDimensions.scaleFor(availableSize: Dp): Float {
    if (!autoScale) return 1f

    return (availableSize.value / scaleBaselineSize.value)
        .coerceIn(minimumScale, maximumScale)
}

private fun Dp.scaledBy(scale: Float): Dp = (value * scale).dp

private fun DrawScope.scaledPx(value: Dp, scale: Float): Float = value.toPx() * scale

private fun BoxWithConstraintsScope.minCompassDimension(): Dp = when {
    maxWidth == Dp.Infinity && maxHeight == Dp.Infinity -> CompassDefaults.MinSize
    maxWidth == Dp.Infinity -> maxHeight
    maxHeight == Dp.Infinity -> maxWidth
    maxWidth < maxHeight -> maxWidth
    else -> maxHeight
}

private fun Offset.pointAt(radius: Float, degrees: Float): Offset {
    val radians = Math.toRadians((degrees - NORTH_OFFSET_DEGREES).toDouble())
    return Offset(
        x = x + radius * cos(x = radians).toFloat(),
        y = y + radius * sin(x = radians).toFloat(),
    )
}


@Composable
@PreviewLightDark
private fun CompassPreview() {
    MaterialTheme() {
        Compass()
    }
}
