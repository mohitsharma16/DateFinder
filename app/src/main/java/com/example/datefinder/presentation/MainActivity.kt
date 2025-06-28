package com.example.datefinder.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.datefinder.presentation.components.ImagePreview
import com.example.datefinder.ui.theme.DateFinderTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DateFinderViewModel

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(this, it)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissionIfNeeded() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            imagePickerLauncher.launch("image/*") // No permission needed on Android 14+
        } else if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = DateFinderViewModel()

        setContent {
            DateFinderTheme {
                val state by viewModel.uiState.collectAsState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Button(onClick = { requestPermissionIfNeeded() }) {
                        Text("Pick Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    state.imageUri?.let { uri ->
                        ImagePreview(
                            imageUri = uri,
                            modifier = Modifier.height(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    state.extractedDate?.let { date ->
                        Text(
                            text = "Extracted Date: $date",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } ?: run {
                        if (state.imageUri != null) {
                            Text(
                                text = "Processing image...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (state.extractedDate == null && state.imageUri != null) {
                        Text(
                            text = "No date found in the image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up TTS resources
        com.example.datefinder.utils.TTSHelper.shutdown()
    }
}