package com.amirmuhsin.listinghelper.ui.s2_product_detail

import CleanedPhotoAdapter
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentProductDetailBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.s5_review_upload.ReviewUploadFragment
import com.amirmuhsin.listinghelper.util.parcelableList
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProductDetailFragment: BaseFragment<FragmentProductDetailBinding, ProductDetailViewModel>(
    FragmentProductDetailBinding::inflate
) {

    override val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(requireContext())
    }

    private lateinit var cleanedPhotosAdapter: CleanedPhotoAdapter

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result?.contents?.let { code ->
            binding.etBarcode.setText(code)
        }
    }

    override fun assignObjects() {
        setFragmentResultListener(RK_CLEANED_PHOTOS) { _, bundle ->
            val cleanedPairs = bundle.parcelableList<PhotoPair>(ARG_IMAGE_URI) ?: emptyList()
            viewModel.setCleanedPhotos(cleanedPairs)
        }
        cleanedPhotosAdapter = CleanedPhotoAdapter(
            requireContext(),
            onPhotoClick = {
                // TODO: open full screen photo viewer
            }, onAddClick = {
                // TODO: open File Chooser
            })
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
            val barcode = it.toString()
            if (barcode.isNotEmpty()) {
                viewModel.onBarcodeChanged(barcode)
            }
        }
        binding.btnReview.setOnClickListener {
            val product = viewModel.fProduct.value
            val hasProduct = product != null
            if (hasProduct) {
                val cleanedPhotos = viewModel.flCleanedPhotos.value.filterIsInstance<PhotoPair>()
                val args = ReviewUploadFragment.createArgs(product.id, cleanedPhotos)
                findNavController().navigate(R.id.action_open_review_upload, args)
            } else {
                showErrorSnackbar("Product not found")
            }
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

        viewModel.flCleanedPhotos.flowWithLifecycle(lifecycle)
            .onEach { cleanedPhotos ->
                cleanedPhotosAdapter.submitList(cleanedPhotos)
                binding.btnReview.isEnabled = cleanedPhotos.size >= 2
            }.launchIn(lifecycleScope)
    }

    private fun openBarcodeScanner() {
        val options = ScanOptions().apply {
            setPrompt("Scan a barcode")
            setBeepEnabled(true)
            setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        }
        barcodeLauncher.launch(options)
    }

    companion object {

        const val RK_CLEANED_PHOTOS = "rk:cleaned_photos"
        const val ARG_IMAGE_URI = "arg:image_uri"
    }
}