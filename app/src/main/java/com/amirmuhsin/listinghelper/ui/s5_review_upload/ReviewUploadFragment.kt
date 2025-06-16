package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentReviewUploadBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.s5_review_upload.list.ReviewUploadAdapter
import com.amirmuhsin.listinghelper.util.parcelableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReviewUploadFragment: BaseFragment<FragmentReviewUploadBinding, ReviewUploadViewModel>(
    FragmentReviewUploadBinding::inflate
) {

    override val viewModel: ReviewUploadViewModel by viewModels { ReviewUploadViewModelFactory(requireContext()) }

    private lateinit var adapter: ReviewUploadAdapter
    private var productItemId: Long = -1L

    override fun assignObjects() {
        productItemId = requireArguments().getLong(ARG_PRODUCT_ITEM_ID, -1L)
        val pairs = requireArguments().parcelableList<PhotoPair>(ARG_PAIRS) ?: emptyList()

        adapter = ReviewUploadAdapter(
            requireContext(),
            onPhotoClick = { pair -> /* open full screen */ },
            onPhotoRemove = { pair -> viewModel.removePair(pair) }
        )
        viewModel.setInitialPairs(pairs)
    }

    override fun prepareUI() {
        binding.rvConfirmation.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvConfirmation.adapter = adapter
        // Enable drag & drop
//        val touchHelper = ItemTouchHelper(viewModel.dragCallback(adapter))
//        touchHelper.attachToRecyclerView(binding.rvConfirmation)
    }

    override fun setListeners() {
        binding.btnUpload.setOnClickListener {
            viewModel.uploadAll(productItemId)
        }
        binding.toolbar.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun setObservers() {
        viewModel.pairs
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { adapter.submitList(it) }
            .launchIn(lifecycleScope)

        viewModel.uploadProgress
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { binding.pbUpload.progress = it }
            .launchIn(lifecycleScope)
    }

    companion object {

        private const val ARG_PAIRS = "arg:pairs"
        private const val ARG_PRODUCT_ITEM_ID = "arg:product_item_id"

        fun createArgs(
            productItemId: Long,
            pairs: List<PhotoPair>
        ) = bundleOf(ARG_PAIRS to pairs, ARG_PRODUCT_ITEM_ID to productItemId)
    }
}

