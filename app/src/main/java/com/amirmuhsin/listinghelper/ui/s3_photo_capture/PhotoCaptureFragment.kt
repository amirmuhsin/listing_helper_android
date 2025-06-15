package com.amirmuhsin.listinghelper.ui.s3_photo_capture

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentPhotoCaptureBinding
import com.amirmuhsin.listinghelper.ui.s4_bg_clean.BgCleanerFragment
import com.amirmuhsin.listinghelper.ui.s3_photo_capture.list.ThumbnailsAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoCaptureFragment: BaseFragment<FragmentPhotoCaptureBinding, PhotoCaptureViewModel>(
    FragmentPhotoCaptureBinding::inflate
) {

    override val viewModel: PhotoCaptureViewModel by viewModels()

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var thumbAdapter: ThumbnailsAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
            } else {
                if (shouldShowRequestPermissionRationale(CAMERA)) {
                    showErrorSnackbar("Camera permission is required to take photos")
                } else {
                    showErrorSnackbar("Camera permission denied. Please enable it in settings.")
                }
                findNavController().popBackStack()
            }
        }

    override fun assignObjects() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        thumbAdapter = ThumbnailsAdapter(requireContext(), {
            // do nothing for now
        }, {
            viewModel.removePhoto(it)
            thumbAdapter.removePhoto(it)
        })

        ProcessCameraProvider.getInstance(requireContext()).also { future ->
            future.addListener({
                cameraProvider = future.get()
                checkCameraPermission()
            }, ContextCompat.getMainExecutor(requireContext()))
        }
    }

    override fun setListeners() {
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDone.setOnClickListener {
            // Grab the current list of URIs from your ViewModel
            val uriList = viewModel.photosFlow.value

            // Convert to an ArrayList<Uri> so it’s Parcelable
            val arrayList = ArrayList<Uri>(uriList)
            val args = BgCleanerFragment.createArgs(arrayList)
            findNavController().navigate(R.id.action_open_bg_removal, args)
        }
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }
    }

    override fun prepareUI() {
        binding.rvThumbnails.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvThumbnails.adapter = thumbAdapter
    }

    override fun setObservers() {
        viewModel.photosFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { list ->
                // update count, done & capture buttons
                binding.tvCount.text = "${list.size} / 15"
                binding.btnDone.isEnabled = list.size in 1..15
                binding.btnCapture.isEnabled = list.size < 15
            }.launchIn(lifecycleScope)
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> bindCameraUseCases()

            shouldShowRequestPermissionRationale(CAMERA) -> {
                showErrorSnackbar("Camera permission is required")
                requestPermissionLauncher.launch(CAMERA)
            }

            else -> requestPermissionLauncher.launch(CAMERA)
        }
    }

    private fun bindCameraUseCases() {
        cameraProvider.unbindAll()

        // 1) Sharper live preview (16:9 or a fixed resolution)
        val preview = Preview.Builder()
            // pick your desired aspect ratio:
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            // or lock to a high-res the device can support:
            // .setTargetResolution(Size(1280, 720))
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
            .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

        // 2) Max-quality captures
        imageCapture = ImageCapture.Builder()
            // ask for the best JPEG
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            // optionally lock resolution if you know your sensor size:
            // .setTargetResolution(Size(4032, 3024))
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        cameraProvider.bindToLifecycle(
            viewLifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    private fun takePhoto() {
        // create temp file
        val photoFile = File.createTempFile(
            "SHOT_${System.currentTimeMillis()}", ".jpg", requireContext().cacheDir
        )
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object: ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    showErrorSnackbar("Capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModel.addPhoto(uri)
                    thumbAdapter.addNewPhoto(uri)
                    binding.rvThumbnails.post {
                        binding.rvThumbnails.smoothScrollToPosition(0)
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        cameraProvider.unbindAll()
    }
}

