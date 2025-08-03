package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentFullScreenImageBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.list.FullScreenImagePagerAdapter
import com.amirmuhsin.listinghelper.util.parcelableList

class FullScreenViewerFragment: BaseFragment<FragmentFullScreenImageBinding, FullScreenViewerViewModel>(
    FragmentFullScreenImageBinding::inflate
) {

    override val viewModel: FullScreenViewerViewModel by viewModels()

    private lateinit var adapter: FullScreenImagePagerAdapter
    private var isUiVisible = true

    override fun assignObjects() {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val photoPairs = arguments?.parcelableList<PhotoPair>(ARG_PHOTO_LIST) ?: emptyList()
        val startIndex = arguments?.getInt(ARG_START_INDEX) ?: 0

        adapter = FullScreenImagePagerAdapter(requireContext(), photoPairs) {
            toggleSystemUI()
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(startIndex.coerceIn(photoPairs.indices), false)
    }

    override fun prepareUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.btnClose) { view, insets ->
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
            findNavController().popBackStack()
        }
        binding.btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Delete image feature is not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleSystemUI() {
        val window = requireActivity().window
        val controller = WindowCompat.getInsetsController(window, binding.root) ?: return

        if (isUiVisible) {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            binding.btnClose.visibility = View.GONE
            binding.clActionPanel.visibility = View.GONE
        } else {
            controller.show(WindowInsetsCompat.Type.navigationBars())
            binding.btnClose.visibility = View.VISIBLE
            binding.clActionPanel.visibility = View.VISIBLE
        }

        isUiVisible = !isUiVisible
    }

    companion object {

        const val ARG_PHOTO_LIST = "arg:photo_list"
        const val ARG_START_INDEX = "arg:start_index"

        fun createArgs(photoList: List<PhotoPair>, startIndex: Int) = Bundle().apply {
            putParcelableArrayList(ARG_PHOTO_LIST, ArrayList(photoList))
            putInt(ARG_START_INDEX, startIndex)
        }
    }
}
