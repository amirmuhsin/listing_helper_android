package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import android.os.Bundle
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

    override fun assignObjects() {
        val photoPairs = arguments?.parcelableList<PhotoPair>(ARG_PHOTO_LIST) ?: emptyList()
        val startIndex = arguments?.getInt(ARG_START_INDEX) ?: 0

        adapter = FullScreenImagePagerAdapter(requireContext(), photoPairs)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(startIndex.coerceIn(photoPairs.indices), false)
    }

    override fun prepareUI() {
        // Optional: toggle system UI visibility here
    }

    override fun setListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
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
