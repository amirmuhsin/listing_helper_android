package com.amirmuhsin.listinghelper.ui.empty

import androidx.fragment.app.viewModels
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentEmptyBinding

class EmptyFragment: BaseFragment<FragmentEmptyBinding, EmptyViewModel>(
    FragmentEmptyBinding::inflate
) {

    override val viewModel: EmptyViewModel by viewModels()

}

// commit from develop
// created branchA
//  - commit#1 from branchA
//  - commit#2 from branchA