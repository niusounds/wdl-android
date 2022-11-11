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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : AppCompatActivity() {

    private var demo: Demo? by mutableStateOf(null)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContent {
            val micPermissionstate = rememberPermissionState(
                permission = android.Manifest.permission.RECORD_AUDIO
            )

            val runningDemoType: DemoType? by remember {
                derivedStateOf {
                    when (demo) {
                        is PitchShift -> DemoType.PitchShifter
                        is SineWaveGeneratorDemo -> DemoType.SineWaveGenerator
                        else -> null
                    }
                }
            }

            MaterialTheme {
                App(
                    running = runningDemoType,
                    onStart = { demoType ->
                        when (demoType) {
                            DemoType.PitchShifter -> {
                                when (micPermissionstate.status) {
                                    is PermissionStatus.Denied -> {
                                        micPermissionstate.launchPermissionRequest()
                                    }
                                    PermissionStatus.Granted -> {
                                        startDemo(PitchShift(this))
                                    }
                                }
                            }
                            DemoType.SineWaveGenerator -> {
                                startDemo(SineWaveGeneratorDemo(this, volumeGain = 0.2))
                            }
                        }
                    },
                    onStop = {
                        stopDemo()
                    }
                )
            }
        }
    }

    private fun startDemo(newDemo: Demo) {
        stopDemo()
        demo = newDemo
        newDemo.start()
    }

    override fun onDestroy() {
        stopDemo()
        super.onDestroy()
    }

    private fun stopDemo() {
        demo?.stop()
        demo = null
    }
}

enum class DemoType {
    PitchShifter,
    SineWaveGenerator,
}

@Composable
private fun App(
    running: DemoType? = null,
    onStart: (DemoType) -> Unit = {},
    onStop: () -> Unit = {},
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
                    enabled = running != DemoType.PitchShifter,
                    onClick = { onStart(DemoType.PitchShifter) },
                ) {
                    Text(text = "Pitch shifter")
                }
                Button(
                    enabled = running != DemoType.SineWaveGenerator,
                    onClick = { onStart(DemoType.SineWaveGenerator) },
                ) {
                    Text(text = "Sine wave generator")
                }

                Button(
                    enabled = running != null,
                    onClick = onStop,
                ) {
                    Text(text = "Stop demo")
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
