package com.amirmuhsin.listinghelper.ui.product_detail

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentProductDetailBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProductDetailFragment: BaseFragment<FragmentProductDetailBinding, ProductDetailViewModel>(
    FragmentProductDetailBinding::inflate
) {

    override val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory()
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result?.contents?.let { code ->
            binding.etBarcode.setText(code)
        }
    }

    override fun assignObjects() {
    }

    override fun setListeners() {
        binding.btnScanBarcode.setOnClickListener {
            openBarcodeScanner()
        }

        binding.btnAddPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_open_photo_capture)
        }

        binding.etBarcode.doAfterTextChanged {
            val barcode = it.toString()
            if (barcode.isNotEmpty()) {
                viewModel.onBarcodeChanged(barcode)
            }
        }
    }

    override fun setObservers() {
        viewModel.fProduct.flowWithLifecycle(lifecycle)
            .onEach { product ->
                if (product != null) {
                    binding.tvProductName.text = product.name
                    binding.tvProductShortDescription.text = product.shortDescription
                }
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
}