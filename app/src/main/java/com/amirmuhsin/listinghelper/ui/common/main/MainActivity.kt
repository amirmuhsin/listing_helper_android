package com.amirmuhsin.listinghelper.ui.common.main

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseActivity
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.ActivityMainBinding
import com.amirmuhsin.listinghelper.ui.common.main.command.MainCommands
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.ProductDetailFragment

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun assignObjects() {
        handleSharedImagesIfAny(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let { handleSharedImagesIfAny(it) }
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is MainCommands.NewProductWithImagesCreated -> {
                openProductDetails(command.productId)
            }
        }
    }

    private fun handleSharedImagesIfAny(intent: Intent) {
        val uris: List<Uri>? = when (intent.action) {
            Intent.ACTION_SEND -> listOfNotNull(intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)
            Intent.ACTION_SEND_MULTIPLE -> intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
            else -> null
        }

        if (!uris.isNullOrEmpty()) {
            viewModel.createNewProductWithPhotos(uris)
        }
    }

    private fun openProductDetails(productId: Long) {
        val args = ProductDetailFragment.createArgs(productId)
        navController.navigate(R.id.productDetailFragment, args)
    }
}
