package com.jet.compass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class CompassState constructor(
    initialHeadingDegrees: Float = 0f,
    initialAvailability: CompassAvailability = CompassAvailability.Checking,
) {
    var headingDegrees: Float by mutableFloatStateOf(
        value = initialHeadingDegrees.normalizeDegrees()
    )
        private set

    var availability: CompassAvailability by mutableStateOf(value = initialAvailability)
        private set

    var accuracy: CompassAccuracy? by mutableStateOf(value = null)
        private set

    val isAvailable: Boolean
        get() = availability == CompassAvailability.Available

    internal fun updateHeading(headingDegrees: Float) {
        this.headingDegrees = headingDegrees.normalizeDegrees()
        availability = CompassAvailability.Available
    }

    internal fun updateAvailability(availability: CompassAvailability) {
        this.availability = availability
    }

    internal fun updateAccuracy(accuracy: CompassAccuracy?) {
        this.accuracy = accuracy
    }
}

enum class CompassAvailability {
    Checking,
    Available,
    NotSupported,
}

enum class CompassAccuracy {
    Unreliable,
    Low,
    Medium,
    High,
}

@Composable
fun rememberCompassState(
    initialHeadingDegrees: Float = 0f,
    initialAvailability: CompassAvailability = CompassAvailability.Checking,
): CompassState = remember {
    CompassState(
        initialHeadingDegrees = initialHeadingDegrees,
        initialAvailability = initialAvailability,
    )
}

private fun Float.normalizeDegrees(): Float {
    val normalized = this % FULL_CIRCLE_DEGREES
    return if (normalized < 0f) normalized + FULL_CIRCLE_DEGREES else normalized
}

private const val FULL_CIRCLE_DEGREES = 360f
