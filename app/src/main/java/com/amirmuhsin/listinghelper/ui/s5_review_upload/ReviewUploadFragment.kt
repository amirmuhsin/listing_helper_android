package com.amirmuhsin.listinghelper.ui.s5_review_upload

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.FragmentReviewUploadBinding
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.FullScreenViewerFragment
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
import com.amirmuhsin.listinghelper.ui.s5_review_upload.list.DragDropCallback
import com.amirmuhsin.listinghelper.ui.s5_review_upload.list.ReviewUploadAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewUploadFragment: BaseFragment<FragmentReviewUploadBinding, ReviewUploadViewModel>(
    FragmentReviewUploadBinding::inflate
) {

    override val viewModel: ReviewUploadViewModel by viewModel()

    private lateinit var adapter: ReviewUploadAdapter
    private var productId: Long = -1L
    private var touchHelper: ItemTouchHelper? = null

    override fun assignObjects() {
        productId = requireArguments().getLong(ARG_PRODUCT_ID, -1L)

        adapter = ReviewUploadAdapter(
            requireContext(),
            onPhotoClick = { clickedPair ->
                val args = FullScreenViewerFragment.createArgs(productId, clickedPair.internalId)
                findNavController().navigate(R.id.action_global_fullScreenImage, args)
            },
            onPhotoRemove = { pair -> viewModel.removePair(pair) },
            startDragListener = { viewHolder -> touchHelper?.startDrag(viewHolder) },
            onReordered = { reorderedList ->
                viewModel.setReorderedPairsSilently(reorderedList)
            }
        )
        viewModel.load(productId) // moved here to avoid swap animation
    }

    override fun prepareUI() {
        binding.rvConfirmation.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvConfirmation.adapter = adapter
    }

    override fun setListeners() {
        binding.btnUpload.setOnClickListener {
            viewModel.uploadAll(productId)
        }
        binding.toolbar.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDone.setOnClickListener {
            findNavController().navigate(
                R.id.action_reviewUpload_to_home
            )
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
                binding.btnDone.visibility = View.GONE
                binding.btnUpload.visibility = View.VISIBLE
                binding.btnUpload.isEnabled = true
                binding.btnUpload.text = "Retry Upload"
            }

            is ReviewUploadCommands.UploadFullyCompleted -> {
                showSuccessSnackbar("All images uploaded successfully!")
                binding.btnDone.visibility = View.VISIBLE
                binding.btnUpload.visibility = View.GONE
            }

            is ReviewUploadCommands.UploadItemProgress -> {
                binding.btnUpload.isEnabled = false
                val percent = (command.uploaded * 100) / command.total
                binding.btnUpload.text = "Uploading ${percent}% (${command.uploaded}/${command.total})"
            }
        }
    }

    companion object {

        private const val ARG_PRODUCT_ID = "arg:product_item_id"

        fun createArgs(
            productId: Long,
        ) = bundleOf(ARG_PRODUCT_ID to productId)
    }
}

