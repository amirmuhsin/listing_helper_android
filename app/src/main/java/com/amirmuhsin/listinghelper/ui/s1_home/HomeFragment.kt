package com.amirmuhsin.listinghelper.ui.s1_home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseFragment
import com.amirmuhsin.listinghelper.databinding.FragmentHomeBinding
import io.cloudx.sdk.AdViewListener
import io.cloudx.sdk.CloudX
import io.cloudx.sdk.CloudXAd
import io.cloudx.sdk.CloudXAdError

class HomeFragment: BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {

    override val viewModel: HomeViewModel by viewModels()

    fun initAds() {
        val bannerAd = CloudX.createBanner(requireActivity(), "pl-android", listener = object: AdViewListener {
            override fun onAdClosedByUser(placementName: String) {
                println("hop: Ad closed by user for placement: $placementName")
            }

            override fun onAdClicked(cloudXAd: CloudXAd) {
                println("hop: Ad clicked")
            }

            override fun onAdHidden(cloudXAd: CloudXAd) {
                println("hop: Ad hidden")
            }

            override fun onAdLoadFailed(cloudXAdError: CloudXAdError) {
                println("hop: Ad load failed")
            }

            override fun onAdLoadSuccess(cloudXAd: CloudXAd) {
                println("hop: Ad load success")
            }

            override fun onAdShowFailed(cloudXAdError: CloudXAdError) {
                println("hop: Ad show failed")
            }

            override fun onAdShowSuccess(cloudXAd: CloudXAd) {
                println("hop: Ad show success")
            }
        })
        if (bannerAd == null) {
            println("hop: Failed to create banner ad")
            return
        } else {
            println("hop: Banner ad created successfully")
        }
        binding.flBannerAd.addView(bannerAd)
        bannerAd.show()
    }

    override fun setListeners() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_open_product_details)
        }
    }
}