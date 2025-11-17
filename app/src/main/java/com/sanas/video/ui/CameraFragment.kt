package com.sanas.video.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sanas.video.R
import com.sanas.video.databinding.FragmentCameraBinding
import com.sanas.video.ui.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var recorder: Recorder
    private lateinit var videoCapture: VideoCapture<Recorder>

    private val viewModel: CameraViewModel by viewModels()

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (hasPermissions()) startCamera()
        else requestPermissions(permissions, 100)
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HD,
                        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                    )
                )
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    videoCapture
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRecord.setOnClickListener {
            viewModel.toggleRecording(recorder)
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_cameraFragment_to_historyFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRecording.collect { recording ->
                    binding.btnRecord.text = if (recording) "STOP" else "RECORD"
                    if (recording) {
                        binding.imgRecordingDot.visibility = View.VISIBLE
                        startBlinking()
                    } else {
                        stopBlinking()
                    }

                }
            }
        }
    }

    private fun startBlinking() {
        val anim = AlphaAnimation(0f, 1f).apply {
            duration = 600
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.imgRecordingDot.startAnimation(anim)
    }

    private fun stopBlinking() {
        binding.imgRecordingDot.clearAnimation()
        binding.imgRecordingDot.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
