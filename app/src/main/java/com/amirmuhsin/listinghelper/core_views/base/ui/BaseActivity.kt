package com.amirmuhsin.listinghelper.core_views.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSplashScreen()

        binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)

        assignObjects()
        setListeners()
        prepareUI()
        setBaseObservers()
        setObservers()
    }

    protected open fun setupSplashScreen() {}
    protected open fun assignObjects() {}
    protected open fun setListeners() {}
    protected open fun prepareUI() {}
    protected open fun setObservers() {}
    protected open fun handleCommand(command: Command){}

    private fun setBaseObservers() {
        viewModel.flCommand.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { command ->
                handleCommand(command)
            }.launchIn(lifecycleScope)
    }

}