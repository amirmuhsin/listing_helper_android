package com.amirmuhsin.listinghelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseActivity
import com.amirmuhsin.listinghelper.databinding.ActivityMainBinding
import kotlin.collections.isNullOrEmpty

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels()

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun assignObjects() {
        handleSharedImagesIfAny(intent)
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleSharedImagesIfAny(it) }
    }

    private fun handleSharedImagesIfAny(intent: Intent) {
        val uris: List<Uri>? = when (intent.action) {
            Intent.ACTION_SEND -> listOfNotNull(intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)
            Intent.ACTION_SEND_MULTIPLE -> intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
            else -> null
        }

        if (!uris.isNullOrEmpty()) {
            // Navigate to your fragment and pass the URIs
            val bundle = Bundle().apply {
                putParcelableArrayList("arg:image_uri", ArrayList(uris))
            }

            navController.navigate(R.id.productDetailFragment, bundle)
        }
    }

}
