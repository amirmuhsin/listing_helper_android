package com.amirmuhsin.listinghelper

import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseActivity
import com.amirmuhsin.listinghelper.databinding.ActivityMainBinding
import io.cloudx.sdk.CloudX
import io.cloudx.sdk.CloudXInitializationListener
import io.cloudx.sdk.CloudXInitializationStatus

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels()

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun assignObjects() {

        val initParams = CloudX.InitializationParams("3qo_qwMH0aaBXUb_rjmga", "https://pro-dev.cloudx.io/sdk", null)
        CloudX.initialize(this, initParams, object: CloudXInitializationListener {
            override fun onCloudXInitializationStatus(status: CloudXInitializationStatus) {
                println("hop: SDK initialized")
            }
        })
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}
