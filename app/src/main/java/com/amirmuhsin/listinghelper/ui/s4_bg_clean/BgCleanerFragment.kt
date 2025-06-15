package com.amirmuhsin.listinghelper.ui.s4_bg_clean

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentBgCleanerBinding
import com.amirmuhsin.listinghelper.networking.PhotoRoomNetworkModule
import com.amirmuhsin.listinghelper.ui.s2_product_detail.ProductDetailFragment
import com.amirmuhsin.listinghelper.ui.s4_bg_clean.list.BgCleanerAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BgCleanerFragment: BaseFragment<FragmentBgCleanerBinding, BgCleanerViewModel>(
    FragmentBgCleanerBinding::inflate
) {

    override val viewModel: BgCleanerViewModel by viewModels {
        BgCleanerViewModelFactory(
            PhotoRoomNetworkModule.photoRoomService,
            requireContext().applicationContext
        )
    }

    private lateinit var pairAdapter: BgCleanerAdapter

    override fun assignObjects() {
        val uriList = requireArguments().getParcelableArrayList<Uri>(ARG_IMAGE_URI) ?: emptyList()

        viewModel.initOriginals(uriList)
        pairAdapter = BgCleanerAdapter(requireContext(), {}, {})
    }

    override fun prepareUI() {
        binding.rvPhotoPairs.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvPhotoPairs.adapter = pairAdapter

        buttonStatePending()
    }

    override fun setListeners() {
        binding.btnProcess.setOnClickListener {
            viewModel.processAllOriginals()
            buttonStateProcessing()
        }
        binding.flToolbar.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDone.setOnClickListener {
            setFragmentResult(
                ProductDetailFragment.RK_CLEANED_PHOTOS,
                bundleOf(ProductDetailFragment.ARG_IMAGE_URI to ArrayList(viewModel.flPairs.value))
            )
            findNavController().popBackStack(R.id.productDetailFragment, false)
        }
    }

    override fun setObservers() {
        viewModel.flPairs
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { list ->
                pairAdapter.submitList(list)
            }.launchIn(lifecycleScope)

        viewModel.flProgress
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { progress ->
                binding.pbProcess.progress = progress
                binding.flToolbar.tvTitle.text = "$progress%"
                if (progress == 100) {
                    buttonStateDone()
                }
            }.launchIn(lifecycleScope)
    }

    private fun buttonStatePending() {
        binding.btnProcess.isEnabled = true
        binding.btnProcess.visibility = View.VISIBLE
        binding.btnDone.visibility = View.GONE
    }

    private fun buttonStateProcessing() {
        binding.btnProcess.text = "Cleaning Background..."
        binding.btnProcess.visibility = View.VISIBLE
        binding.btnProcess.isEnabled = false
        binding.btnDone.visibility = View.GONE
    }

    private fun buttonStateDone() {
        binding.btnProcess.visibility = View.GONE
        binding.btnDone.visibility = View.VISIBLE
    }

    companion object {

        private const val ARG_IMAGE_URI: String = "args:image_uri"

        fun createArgs(uriList: ArrayList<Uri>): Bundle {
            return bundleOf(ARG_IMAGE_URI to uriList)
        }
    }

}

