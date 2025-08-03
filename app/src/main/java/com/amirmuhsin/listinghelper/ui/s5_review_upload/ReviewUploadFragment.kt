package com.amirmuhsin.listinghelper.ui.s5_review_upload

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.FragmentReviewUploadBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
import com.amirmuhsin.listinghelper.ui.s5_review_upload.list.DragDropCallback
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
    private var touchHelper: ItemTouchHelper? = null

    override fun assignObjects() {
        productItemId = requireArguments().getLong(ARG_PRODUCT_ITEM_ID, -1L)
        val pairs = requireArguments().parcelableList<PhotoPair>(ARG_PAIRS) ?: emptyList()

        adapter = ReviewUploadAdapter(
            requireContext(),
            onPhotoClick = { pair ->

            },
            onPhotoRemove = { pair -> viewModel.removePair(pair) },
            startDragListener = { viewHolder -> touchHelper?.startDrag(viewHolder) },
            onReordered = { reorderedList -> viewModel.setReorderedPairsSilently(reorderedList) }
        )
        viewModel.setInitialPairs(pairs)
    }

    override fun prepareUI() {
        binding.rvConfirmation.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvConfirmation.adapter = adapter
    }

    override fun setListeners() {
        binding.btnUpload.setOnClickListener {
            viewModel.uploadAll(productItemId)
        }
        binding.toolbar.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDone.setOnClickListener {
            findNavController().popBackStack()
        }
        val callback = DragDropCallback(adapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper?.attachToRecyclerView(binding.rvConfirmation)
    }

    override fun setObservers() {
        viewModel.pairs
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                adapter.submitList(it)
            }
            .launchIn(lifecycleScope)

        viewModel.uploadProgress
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.pbUpload.progress = it
                binding.toolbar.tvTitle.text = "$it%"
            }.launchIn(lifecycleScope)
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is ReviewUploadCommands.UploadCompleted -> {
                val areAllUploaded = command.uploaded == command.total
                if (areAllUploaded) {
                    showSuccessSnackbar("All images uploaded successfully!")
                    binding.btnDone.visibility = View.VISIBLE
                    binding.btnUpload.visibility = View.GONE

                    findNavController().navigate(
                        R.id.action_reviewUpload_to_home
                    )

                } else {
                    showErrorSnackbar("Some images failed to upload. Please try again.")
                    binding.btnDone.visibility = View.GONE
                    binding.btnUpload.visibility = View.VISIBLE
                    binding.btnUpload.isEnabled = true
                    binding.btnUpload.text = "Retry Upload"
                }
            }

            is ReviewUploadCommands.UploadItemProgress -> {
                binding.btnUpload.isEnabled = false
                val percent = (command.uploaded * 100) / command.total
                binding.btnUpload.text = "Uploading ${percent}% (${command.uploaded}/${command.total})"
            }
        }
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

