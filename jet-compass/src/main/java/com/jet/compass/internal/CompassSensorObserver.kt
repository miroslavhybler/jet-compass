package com.jet.compass.internal

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.jet.compass.CompassAccuracy
import com.jet.compass.CompassAvailability
import com.jet.compass.CompassState


private const val VECTOR_SIZE = 3
private const val ROTATION_MATRIX_SIZE = 9
private const val ORIENTATION_ANGLES_SIZE = 3
private const val AZIMUTH_INDEX = 0
private const val LOW_PASS_ALPHA = 0.15f


@Composable
internal fun CompassSensorEffect(state: CompassState) {
    val context = LocalContext.current.applicationContext
    val observer = remember(key1 = context) { CompassSensorObserver(context) }

    DisposableEffect(key1 = observer, key2 = state) {
        observer.start(state)
        onDispose { observer.stop() }
    }
}

private class CompassSensorObserver(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
    private val orientationAngles = FloatArray(ORIENTATION_ANGLES_SIZE)
    private val gravityValues = FloatArray(VECTOR_SIZE)
    private val magneticValues = FloatArray(VECTOR_SIZE)

    private var state: CompassState? = null
    private var hasGravityValues = false
    private var hasMagneticValues = false

    fun start(state: CompassState) {
        stop()
        this.state = state

        val manager = sensorManager
        if (manager == null) {
            state.updateAvailability(CompassAvailability.NotSupported)
            return
        }

        val rotationVectorSensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor != null) {
            val registered = manager.registerListener(
                this,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI,
            )
            state.updateAvailability(
                if (registered) CompassAvailability.Available else CompassAvailability.NotSupported,
            )
            return
        }

        val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accelerometer == null || magnetometer == null) {
            state.updateAvailability(CompassAvailability.NotSupported)
            return
        }

        val accelerometerRegistered = manager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI,
        )
        val magnetometerRegistered = manager.registerListener(
            this,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI,
        )

        if (accelerometerRegistered && magnetometerRegistered) {
            state.updateAvailability(CompassAvailability.Available)
        } else {
            manager.unregisterListener(this)
            state.updateAvailability(CompassAvailability.NotSupported)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        state = null
        hasGravityValues = false
        hasMagneticValues = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> updateFromRotationVector(event.values)
            Sensor.TYPE_ACCELEROMETER -> {
                lowPass(event.values, gravityValues)
                hasGravityValues = true
                updateFromGravityAndMagneticValues()
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                lowPass(event.values, magneticValues)
                hasMagneticValues = true
                updateFromGravityAndMagneticValues()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        state?.updateAccuracy(accuracy.toCompassAccuracy())
    }

    private fun updateFromRotationVector(rotationVector: FloatArray) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        updateHeadingFromRotationMatrix()
    }

    private fun updateFromGravityAndMagneticValues() {
        if (!hasGravityValues || !hasMagneticValues) return

        val hasRotationMatrix = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            gravityValues,
            magneticValues,
        )
        if (hasRotationMatrix) {
            updateHeadingFromRotationMatrix()
        }
    }

    private fun updateHeadingFromRotationMatrix() {
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val headingDegrees = Math.toDegrees(orientationAngles[AZIMUTH_INDEX].toDouble()).toFloat()
        state?.updateHeading(headingDegrees)
    }

    private fun lowPass(input: FloatArray, output: FloatArray) {
        for (index in 0 until VECTOR_SIZE) {
            output[index] += LOW_PASS_ALPHA * (input[index] - output[index])
        }
    }
}

private fun Int.toCompassAccuracy(): CompassAccuracy = when (this) {
    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> CompassAccuracy.High
    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> CompassAccuracy.Medium
    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassAccuracy.Low
    else -> CompassAccuracy.Unreliable
}

