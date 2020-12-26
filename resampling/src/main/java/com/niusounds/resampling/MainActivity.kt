package com.niusounds.resampling

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.niusounds.resampling.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    viewModel.togglePlaying()
                }
            }
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel

            val items = resources.getIntArray(R.array.sample_rates)
            inputSampleRate.dropdown.apply {
                setAdapter(
                    ArrayAdapter(this@MainActivity, R.layout.list_item, items.map { it.toString() })
                )
                setText(items.first { it == viewModel.inputSampleRate.value }.toString(), false)
                addTextChangedListener { text ->
                    text.toString().toIntOrNull()?.let {
                        viewModel.inputSampleRate.value = it
                    }
                }
            }

            outputSampleRate.dropdown.apply {
                setAdapter(
                    ArrayAdapter(this@MainActivity, R.layout.list_item, items.map { it.toString() })
                )
                setText(items.first { it == viewModel.outputSampleRate.value }.toString(), false)
                addTextChangedListener { text ->
                    text.toString().toIntOrNull()?.let {
                        viewModel.outputSampleRate.value = it
                    }
                }
            }

            playStopButton.setOnClickListener {
                checkPermission(requestPermission)
            }
        }
    }

    private fun checkPermission(requestPermission: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.togglePlaying()
        } else {
            requestPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}