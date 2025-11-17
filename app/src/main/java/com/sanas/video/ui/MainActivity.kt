package com.sanas.video.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sanas.video.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissions = buildList {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            // For Android 12 and below
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->

            val allGranted = result.values.all { it }

            if (allGranted) {
                // All good -> NavHost loads CameraFragment automatically
            } else {
                finish() // Close the app
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        if (!hasAllPermissions()) {
            permissionLauncher.launch(permissions)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all { perm ->
            ContextCompat.checkSelfPermission(this, perm) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }
}
