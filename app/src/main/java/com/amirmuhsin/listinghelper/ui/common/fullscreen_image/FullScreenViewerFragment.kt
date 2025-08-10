package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.FragmentFullScreenImageBinding
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.command.FullScreenCommands
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.list.FullScreenImagePagerAdapter
import com.amirmuhsin.listinghelper.util.getImageResolution
import com.amirmuhsin.listinghelper.util.getImageSizeInBytes
import com.amirmuhsin.listinghelper.util.getReadableSize
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FullScreenViewerFragment: BaseFragment<FragmentFullScreenImageBinding, FullScreenViewerViewModel>(
    FragmentFullScreenImageBinding::inflate
) {

    override val viewModel: FullScreenViewerViewModel by viewModels {
        FullScreenViewerViewModelFactory(
            requireContext().applicationContext,
            productId = arguments?.getLong(ARG_PRODUCT_ID) ?: -1,
            startPhotoPairId = arguments?.getString(ARG_START_PHOTO_ID) ?: ""
        )
    }

    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goBackList()
        }
    }

    private lateinit var adapter: FullScreenImagePagerAdapter
    private var isUiVisible = true

    override fun assignObjects() {
        setFitSystemWindows(false)

        adapter = FullScreenImagePagerAdapter {
            toggleSystemUI()
        }
        binding.viewPager.adapter = adapter
    }

    override fun prepareUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clToolbar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topInset
                marginEnd = 0
            }
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topInset
            }
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.clActionPanel) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = bottomInset
            }
            insets
        }
    }

    override fun setListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
        binding.btnClose.setOnClickListener {
            goBackList()
        }
        binding.btnDelete.setOnClickListener {
            val currentItemPosition = binding.viewPager.currentItem
            viewModel.deletePhotoPair(currentItemPosition)
        }
        // Register the back press callback
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePhotoMeta(position)
            }
        })
    }

    override fun setObservers() {
        viewModel.flPhotos.flowWithLifecycle(lifecycle)
            .onEach { images ->
                adapter.submitList(images)
            }.launchIn(lifecycleScope)

        viewModel.flStartIndexFlow.flowWithLifecycle(lifecycle)
            .onEach { startIndex ->
                if (startIndex == -1) return@onEach
                binding.viewPager.setCurrentItem(startIndex.coerceIn(viewModel.flPhotos.value.indices), false)
                updatePhotoMeta(startIndex)
            }.launchIn(lifecycleScope)
    }

    override fun handleCommand(command: Command) {
        super.handleCommand(command)
        if (command is FullScreenCommands.AllImagesDeleted) {
            goBackList()
        } else if (command is FullScreenCommands.ImageDeleted) {
            val currentItemPosition = binding.viewPager.currentItem
            updatePhotoMeta(currentItemPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
        setFitSystemWindows(true)
    }

    private fun updatePhotoMeta(position: Int) {
        val list = viewModel.flPhotos.value
        val pair = list.getOrNull(position) ?: return

        val fileSizeBytes = getImageSizeInBytes(requireContext(), pair.cleanedUri)
        val resolution = getImageResolution(requireContext(), pair.cleanedUri)

        val imageResolution = resolution?.let { "${it.first} x ${it.second}" } ?: "Unknown"
        val imageSizeInKB = getReadableSize(fileSizeBytes)
        binding.tvImageResolution.text = imageResolution
        binding.tvImageSize.text = imageSizeInKB

        binding.tvCount.text = "${position + 1} / ${list.size}"
    }

    private fun toggleSystemUI() {
        val window = requireActivity().window
        val controller = WindowCompat.getInsetsController(window, binding.root) ?: return

        if (isUiVisible) {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            binding.clToolbar.visibility = View.GONE
            binding.clActionPanel.visibility = View.GONE
        } else {
            controller.show(WindowInsetsCompat.Type.navigationBars())
            binding.clToolbar.visibility = View.VISIBLE
            binding.clActionPanel.visibility = View.VISIBLE
        }

        isUiVisible = !isUiVisible
    }

    private fun goBackList() {
        if (viewModel.isListChanged) {
            setFragmentResult(RK_PHOTO_LIST_CHANGED, Bundle.EMPTY)
        }
        findNavController().popBackStack()
    }

    private fun setFitSystemWindows(isEnabled: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, isEnabled)
    }

    companion object {

        const val ARG_PRODUCT_ID = "arg:product_id"
        const val ARG_START_PHOTO_ID = "arg:start_photo_id"

        const val RK_PHOTO_LIST_CHANGED = "rk:photo_list"

        fun createArgs(productId: Long, startPhotoId: String) = Bundle().apply {
            putLong(ARG_PRODUCT_ID, productId)
            putString(ARG_START_PHOTO_ID, startPhotoId)
        }
    }
}
