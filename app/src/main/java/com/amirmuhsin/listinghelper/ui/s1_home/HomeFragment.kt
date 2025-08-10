package com.amirmuhsin.listinghelper.ui.s1_home

import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.data.networking.PhotoRoomNetworkModule
import com.amirmuhsin.listinghelper.databinding.FragmentHomeBinding
import com.amirmuhsin.listinghelper.ui.s1_home.command.HomeCommands
import com.amirmuhsin.listinghelper.ui.s1_home.list.ProductListAdapter
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.ProductDetailFragment
import kotlinx.coroutines.launch

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

        binding.rvProducts.adapter = productListAdapter
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    override fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fProducts.collect { productListAdapter.submitList(it) }
            }
        }
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is HomeCommands.NewProductCreated -> {
                openProductDetails(command.productId)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllProducts()
    }

    private fun openProductDetails(productId: Long) {
        val args = ProductDetailFragment.createArgs(productId)
        findNavController().navigate(R.id.action_open_product_details, args)
    }
}