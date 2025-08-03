package com.amirmuhsin.listinghelper.ui.s1_home

import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.data.networking.PhotoRoomNetworkModule
import com.amirmuhsin.listinghelper.databinding.FragmentHomeBinding

class HomeFragment: BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {

    override val viewModel: HomeViewModel by viewModels()

    override fun assignObjects() {
        binding.chSandbox.isChecked = PhotoRoomNetworkModule.isSandbox
    }

    override fun setListeners() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_open_product_details)
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
}