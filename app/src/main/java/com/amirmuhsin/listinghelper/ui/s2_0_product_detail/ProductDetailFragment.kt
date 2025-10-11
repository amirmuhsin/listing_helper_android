package com.amirmuhsin.listinghelper.ui.s2_0_product_detail

import CleanedPhotoAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentProductDetailBinding
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.FullScreenViewerFragment
import com.amirmuhsin.listinghelper.ui.s2_1_barcode_scanner.BarcodeScannerActivity
import com.amirmuhsin.listinghelper.ui.s5_review_upload.ReviewUploadFragment
import com.amirmuhsin.listinghelper.util.ImageStore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailFragment: BaseFragment<FragmentProductDetailBinding, ProductDetailViewModel>(
    FragmentProductDetailBinding::inflate
) {

    override val viewModel: ProductDetailViewModel by viewModel()

    private lateinit var cleanedPhotosAdapter: CleanedPhotoAdapter

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result?.contents?.let { code ->
            binding.etBarcode.setText(code)
        }
    }

    private var productId: Long = -1L
    private var isBarcodeLaunchRequired = false

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) {
                val localUris = uris.map { ImageStore.copyToAppFiles(requireContext(), it) }
                viewModel.addNewCleanedPhotos(localUris)
            }
        }

    override fun assignObjects() {
        productId = arguments?.getLong(ARG_PRODUCT_ID, -1L) ?: -1L
        isBarcodeLaunchRequired = arguments?.getBoolean(ARG_IS_BARCODE_LAUNCH_REQUIRED, false) ?: false

        cleanedPhotosAdapter = CleanedPhotoAdapter(
            requireContext(),
            onPhotoClick = { clickedPhotoPair ->
                val args = FullScreenViewerFragment.createArgs(productId, clickedPhotoPair.internalId)
                findNavController().navigate(R.id.action_global_fullScreenImage, args)
            }, onAddClick = {
                pickImagesLauncher.launch("image/*")
            })
        cleanedPhotosAdapter.setHasStableIds(true)
    }

    override fun prepareUI() {
        binding.rvCleanedPhotos.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        binding.rvCleanedPhotos.adapter = cleanedPhotosAdapter
    }

    override fun setListeners() {
        binding.btnScanBarcode.setOnClickListener {
            openBarcodeScanner()
        }
        binding.fabAddPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_open_photo_capture)
        }
        binding.etBarcode.doAfterTextChanged {
            val barcodeOfSku = it.toString()
            if (barcodeOfSku.isNotEmpty()) {
                viewModel.fetchAndUpdateBySku(barcodeOfSku)
            }
        }
        binding.btnReview.setOnClickListener {
            val args = ReviewUploadFragment.createArgs(productId)
            findNavController().navigate(R.id.action_open_review_upload, args)
        }
        binding.toolbar.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObservers() {
        viewModel.fProduct.flowWithLifecycle(lifecycle)
            .onEach { product ->
                if (product != null) {
                    binding.tvProductName.text = product.name
                    binding.tvProductShortDescription.text = product.shortDescription
                    binding.tvProductName.visibility = View.VISIBLE
                    binding.tvProductShortDescription.visibility = View.VISIBLE
                }
            }.launchIn(lifecycleScope)

        viewModel.flPhotoPairs.flowWithLifecycle(lifecycle)
            .onEach { photoPairs ->
                cleanedPhotosAdapter.submitList(photoPairs)
                binding.btnReview.isEnabled = photoPairs.size >= 2 // todo: product status OR non uploaded image count
            }.launchIn(lifecycleScope)

        viewModel.getLocalProductByIdInFull(productId)

        if (isBarcodeLaunchRequired) {
            isBarcodeLaunchRequired = false
            requireArguments().putBoolean(ARG_IS_BARCODE_LAUNCH_REQUIRED, false)
            openBarcodeScanner()
        }
    }

    private fun openBarcodeScanner() {
        val options = ScanOptions().apply {
            setPrompt("Scan a barcode")
            setBeepEnabled(true)
            setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            setCaptureActivity(BarcodeScannerActivity::class.java)
        }
        barcodeLauncher.launch(options)
    }

    companion object {

        const val RK_CLEANED_PHOTOS = "rk:cleaned_photos"
        const val ARG_IMAGE_URI = "arg:image_uri"

        private const val ARG_PRODUCT_ID = "arg:product_id"
        private const val ARG_IS_BARCODE_LAUNCH_REQUIRED = "arg:is_barcode_launch_required"

        fun createArgs(productId: Long, isBarcodeLaunchRequired: Boolean = false): Bundle {
            return Bundle().apply {
                putLong(ARG_PRODUCT_ID, productId)
                putBoolean(ARG_IS_BARCODE_LAUNCH_REQUIRED, isBarcodeLaunchRequired)
            }
        }
    }
}