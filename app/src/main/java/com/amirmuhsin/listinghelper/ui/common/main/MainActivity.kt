package com.amirmuhsin.listinghelper.ui.common.main

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseActivity
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.databinding.ActivityMainBinding
import com.amirmuhsin.listinghelper.ui.common.main.command.MainCommands
import com.amirmuhsin.listinghelper.ui.s2_0_product_detail.ProductDetailFragment
import com.amirmuhsin.listinghelper.util.ImageStore

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun assignObjects() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, 0)
            windowInsets
        }

        handleSharedImagesIfAny(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSharedImagesIfAny(intent)
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is MainCommands.NewProductWithImagesCreated -> {
                openProductDetails(command.productId)
            }
        }
    }

    private fun handleSharedImagesIfAny(intent: Intent) {
        val incoming: List<Uri>? = when (intent.action) {
            Intent.ACTION_SEND ->
                listOfNotNull(
                    if (android.os.Build.VERSION.SDK_INT >= 33)
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    else @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                )

            Intent.ACTION_SEND_MULTIPLE ->
                if (android.os.Build.VERSION.SDK_INT >= 33)
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                else @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)

            else -> null
        }

        if (!incoming.isNullOrEmpty()) {
            val localUris = incoming.map { ImageStore.copyToAppFiles(this, it) }
            viewModel.createNewProductWithPhotos(localUris)
        }
    }

    private fun openProductDetails(productId: Long) {
        val args = ProductDetailFragment.createArgs(productId, true)
        navController.navigate(R.id.productDetailFragment, args)
    }
}
