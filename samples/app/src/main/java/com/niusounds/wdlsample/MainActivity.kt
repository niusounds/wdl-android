package com.niusounds.wdlsample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : AppCompatActivity() {

    private var demo: Demo? = null

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContent {
            val micPermissionstate = rememberPermissionState(
                permission = android.Manifest.permission.RECORD_AUDIO
            )

            MaterialTheme {
                App(
                    onStartPitchShiftDemo = {
                        when (micPermissionstate.status) {
                            is PermissionStatus.Denied -> {
                                micPermissionstate.launchPermissionRequest()
                            }
                            PermissionStatus.Granted -> {
                                startDemo(PitchShift(this))
                            }
                        }
                    },
                    onStartSineWaveGenerator = {
                        startDemo(SineWaveGeneratorDemo(this, volumeGain = 0.2))
                    }
                )
            }
        }
    }

    private fun startDemo(newDemo: Demo) {
        demo?.stop()
        demo = newDemo
        newDemo.start()
    }

    override fun onDestroy() {
        demo?.stop()
        demo = null
        super.onDestroy()
    }
}

@Composable
private fun App(
    onStartPitchShiftDemo: () -> Unit = {},
    onStartSineWaveGenerator: () -> Unit = {},
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("WDL Sample") })
    }) { paddings ->
        Box(
            Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onStartPitchShiftDemo,
                ) {
                    Text(text = "Pitch shifter")
                }
                Button(
                    onClick = onStartSineWaveGenerator,
                ) {
                    Text(text = "Sine wave generator")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewApp() {
    MaterialTheme {
        App()
    }
}
