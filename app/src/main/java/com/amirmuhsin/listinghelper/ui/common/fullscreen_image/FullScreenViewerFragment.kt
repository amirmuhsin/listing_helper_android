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
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.FragmentFullScreenImageBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.command.FullScreenCommands
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.list.FullScreenImagePagerAdapter
import com.amirmuhsin.listinghelper.util.parcelableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FullScreenViewerFragment: BaseFragment<FragmentFullScreenImageBinding, FullScreenViewerViewModel>(
    FragmentFullScreenImageBinding::inflate
) {

    override val viewModel: FullScreenViewerViewModel by viewModels {
        FullScreenViewerViewModelFactory(
            photoPairs = arguments?.parcelableList(ARG_PHOTO_LIST) ?: emptyList(),
            startIndex = arguments?.getInt(ARG_START_INDEX) ?: 0
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
        binding.btnClose.setOnClickListener {
            goBackList()
        }
        binding.btnDelete.setOnClickListener {
            val currentItemPosition = binding.viewPager.currentItem
            viewModel.deletePhotoPair(currentItemPosition)
        }
        // Register the back press callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
        setFitSystemWindows(true)
    }

    override fun setObservers() {
        viewModel.flPhotos.flowWithLifecycle(lifecycle)
            .onEach { images ->
                adapter.submitList(images)
            }.launchIn(lifecycleScope)

        viewModel.flStartIndexFlow.flowWithLifecycle(lifecycle)
            .onEach { startIndex ->
                binding.viewPager.setCurrentItem(startIndex.coerceIn(viewModel.flPhotos.value.indices), false)
            }.launchIn(lifecycleScope)
    }

    override fun handleCommand(command: Command) {
        super.handleCommand(command)
        if (command is FullScreenCommands.AllImagesDeleted) {
            goBackList()
        }
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
        val latestList = viewModel.flPhotos.value
        setFragmentResult(RK_PHOTO_LIST, Bundle().apply {
            putParcelableArrayList(ARG_PHOTO_LIST, ArrayList(latestList))
        })
        findNavController().popBackStack()
    }

    private fun setFitSystemWindows(isEnabled: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, isEnabled)
    }

    companion object {

        const val ARG_PHOTO_LIST = "arg:photo_list"
        const val ARG_START_INDEX = "arg:start_index"
        const val RK_PHOTO_LIST = "rk:photo_list"

        fun createArgs(photoList: List<PhotoPair>, startIndex: Int) = Bundle().apply {
            putParcelableArrayList(ARG_PHOTO_LIST, ArrayList(photoList))
            putInt(ARG_START_INDEX, startIndex)
        }
    }
}
