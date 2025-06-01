package com.amirmuhsin.listinghelper.ui.bg_removal

import androidx.fragment.app.viewModels
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentBgRemovalBinding

class BgRemovalFragment: BaseFragment<FragmentBgRemovalBinding, BgRemovalViewModel>(
    FragmentBgRemovalBinding::inflate
) {

    override val viewModel: BgRemovalViewModel by viewModels()

}

