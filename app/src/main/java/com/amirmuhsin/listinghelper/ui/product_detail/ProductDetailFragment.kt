package com.amirmuhsin.listinghelper.ui.product_detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentProductDetailBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ProductDetailFragment: BaseFragment<FragmentProductDetailBinding, ProductDetailViewModel>(
    FragmentProductDetailBinding::inflate
) {

    override val viewModel: ProductDetailViewModel by viewModels()

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result?.contents?.let { code ->
            binding.etBarcode.setText(code)
        }
    }

    override fun assignObjects() {
    }

    override fun setListeners() {
        binding.btnScanBarcode.setOnClickListener {
            val options = ScanOptions().apply {
                setPrompt("Scan a barcode")
                setBeepEnabled(true)
                setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            }
            barcodeLauncher.launch(options)
        }
        binding.btnAddPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_open_photo_capture)
        }
    }
}