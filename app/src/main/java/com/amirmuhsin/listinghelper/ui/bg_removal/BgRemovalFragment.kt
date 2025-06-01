package com.amirmuhsin.listinghelper.ui.bg_removal

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentBgRemovalBinding
import com.amirmuhsin.listinghelper.networking.NetworkModule
import com.amirmuhsin.listinghelper.ui.bg_removal.list.PhotoPairAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BgRemovalFragment: BaseFragment<FragmentBgRemovalBinding, BgRemovalViewModel>(
    FragmentBgRemovalBinding::inflate
) {

    override val viewModel: BgRemovalViewModel by viewModels {
        BgRemovalViewModelFactory(
            NetworkModule.photoRoomService,
            requireContext().applicationContext
        )
    }

    private lateinit var pairAdapter: PhotoPairAdapter

    override fun assignObjects() {
        val uriList = requireArguments().getParcelableArrayList<Uri>(ARG_IMAGE_URI) ?: emptyList()

        viewModel.initOriginals(uriList)
        println("hop: initial list: $uriList")
        pairAdapter = PhotoPairAdapter(requireContext(), {}, {})
    }

    override fun prepareUI() {
        binding.rvPhotoPairs.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvPhotoPairs.adapter = pairAdapter
    }

    override fun setListeners() {
        binding.btnProcess.setOnClickListener {
            viewModel.processAllOriginals()
        }
    }

    override fun setObservers() {
        viewModel.pairs
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { list ->
                pairAdapter.setData(list)
            }.launchIn(lifecycleScope)

        viewModel.updatedPair
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { updatedPair ->
                pairAdapter.update(updatedPair)
            }.launchIn(lifecycleScope)
    }

    companion object {

        private const val ARG_IMAGE_URI: String = "args:image_uri"

        fun createArgs(uriList: ArrayList<Uri>): Bundle {
            return bundleOf(ARG_IMAGE_URI to uriList)
        }
    }

}

