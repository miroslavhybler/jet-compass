package com.jet.compass

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

fun Location.compassDirectionTo(destination: Location): Float {
    return calculateCompassDirection(
        origin = this,
        destination = destination,
    )
}

fun calculateCompassDirection(
    origin: Location,
    destination: Location,
): Float {
    return calculateCompassDirection(
        originLatitude = origin.latitude,
        originLongitude = origin.longitude,
        destinationLatitude = destination.latitude,
        destinationLongitude = destination.longitude,
    )
}

fun calculateCompassDirection(
    originLatitude: Double,
    originLongitude: Double,
    destinationLatitude: Double,
    destinationLongitude: Double,
): Float {
    val originLatitudeRadians = Math.toRadians(originLatitude)
    val destinationLatitudeRadians = Math.toRadians(destinationLatitude)
    val longitudeDeltaRadians = Math.toRadians(destinationLongitude - originLongitude)

    val y = sin(x=longitudeDeltaRadians) * cos(x=destinationLatitudeRadians)
    val x = cos(x=originLatitudeRadians) * sin(x=destinationLatitudeRadians) -
        sin(x=originLatitudeRadians) * cos(x=destinationLatitudeRadians) * cos(x=longitudeDeltaRadians)

    return normalizeCompassDegrees(degrees= Math.toDegrees(atan2(y=y, x=x)).toFloat())
}

fun normalizeCompassDegrees(degrees: Float): Float {
    val normalized = degrees % FULL_CIRCLE_DEGREES
    return if (normalized < 0f) normalized + FULL_CIRCLE_DEGREES else normalized
}

private fun Float.normalizeDegrees(): Float = normalizeCompassDegrees(degrees = this)

private const val FULL_CIRCLE_DEGREES = 360f
