package com.niusounds.simplepitchshifter.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.niusounds.simplepitchshifter.R
import com.niusounds.simplepitchshifter.databinding.ControlFragmentBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ControlFragment : Fragment(R.layout.control_fragment) {
    private val viewModel: ControlViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ControlFragmentBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            pitchSlider.addOnChangeListener { _, semitone, fromUser ->
                if (fromUser) {
                    viewModel.onChangeSlider(semitone)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.permissionDialogEvent.collect { permission ->
                requestPermissions(arrayOf(permission), permissionRequestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode -> viewModel.onRequestPermissionsResult(permissions, grantResults)
        }
    }

    companion object {
        private const val permissionRequestCode = 1010
    }
}