package com.amirmuhsin.listinghelper.ui.empty

import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentEmptyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmptyFragment: BaseFragment<FragmentEmptyBinding, EmptyViewModel>(
    FragmentEmptyBinding::inflate
) {

    override val viewModel: EmptyViewModel by viewModel()

}

// commit from develop + change AFTER branchA created
// created branchA
//  - commit#1 from branchA
//  - commit#2 from branchA
//  - commit#3 from branchA

// created branchB
//  - commit#1 from branchB
//  - commit#2 from branchB