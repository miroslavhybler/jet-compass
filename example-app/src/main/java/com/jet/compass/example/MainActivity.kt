package com.jet.compass.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jet.compass.Compass
import com.jet.compass.CompassAvailability
import com.jet.compass.rememberCompassState
import com.jet.compass.example.ui.theme.JetCompassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetCompassTheme {
                Scaffold { innerPadding ->
                    ExampleCompass(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExampleCompass(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Compass(modifier = Modifier.size(300.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleCompassPreview() {
    JetCompassTheme {
        Compass(
            modifier = Modifier.size(300.dp),
            state = rememberCompassState(
                initialHeadingDegrees = 35f,
                initialAvailability = CompassAvailability.Available,
            ),
        )
    }
}
