package com.amirmuhsin.listinghelper.ui.s1_home

import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.data.networking.PhotoRoomNetworkModule
import com.amirmuhsin.listinghelper.databinding.FragmentHomeBinding
import com.amirmuhsin.listinghelper.ui.s1_home.command.HomeCommands
import com.amirmuhsin.listinghelper.ui.s1_home.list.ProductListAdapter
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.ProductDetailFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment: BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {

    override val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(requireContext().applicationContext) }

    private lateinit var productListAdapter: ProductListAdapter

    override fun assignObjects() {
        binding.chSandbox.isChecked = PhotoRoomNetworkModule.isSandbox
        productListAdapter = ProductListAdapter(
            onProductClick = { product ->
                openProductDetails(product.id)
            }
        )
    }

    override fun setListeners() {
        binding.fabAdd.setOnClickListener {
            viewModel.createNewProduct()
        }

        binding.chSandbox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    PhotoRoomNetworkModule.isSandbox = true
                    Toast.makeText(requireContext(), "Sandbox mode ON", Toast.LENGTH_SHORT).show()
                } else {
                    PhotoRoomNetworkModule.isSandbox = false
                    Toast.makeText(requireContext(), "Sandbox mode OFF", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun prepareUI() {
        val packageManager = requireContext().packageManager
        val packageName = requireContext().packageName
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName

        binding.tvVersion.text = "Version: $versionName | 3-August-2025"
    }

    override fun setObservers() {
        viewModel.fProducts.flowWithLifecycle(lifecycle)
            .onEach { list ->
                productListAdapter.submitList(list)
            }.launchIn(lifecycleScope)
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is HomeCommands.NewProductCreated -> {
                openProductDetails(command.productId)
            }
        }
    }

    private fun openProductDetails(productId: Long) {
        val args = ProductDetailFragment.createArgs(productId)
        findNavController().navigate(R.id.action_open_product_details, args)
    }
}