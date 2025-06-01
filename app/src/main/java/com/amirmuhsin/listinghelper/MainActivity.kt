package com.amirmuhsin.listinghelper

import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseActivity
import com.amirmuhsin.listinghelper.databinding.ActivityMainBinding

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels()

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}
