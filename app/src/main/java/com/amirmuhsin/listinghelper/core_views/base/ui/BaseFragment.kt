package com.amirmuhsin.listinghelper.core_views.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import com.amirmuhsin.listinghelper.core_views.progress.ProcessProgressDialog
import com.amirmuhsin.listinghelper.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
): Fragment() {

    protected abstract val viewModel: VM

    protected val binding get() = _binding!!
    private var _binding: VB? = null

    private var progressDialog: ProcessProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)

        assignObjects()

        return _binding?.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        prepareUI()
        setBaseObservers()
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        hideProgressDialog()
    }

    protected open fun assignObjects() {}
    protected open fun setListeners() {}
    protected open fun prepareUI() {}
    protected open fun setObservers() {}
    protected open fun handleCommand(command: Command) {}

    private fun setBaseObservers() {
        viewModel.flLoading.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { isLoading ->
                if (isLoading) showProgressDialog() else hideProgressDialog()
            }.launchIn(lifecycleScope)

        viewModel.flCommand.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { command ->
                handleCommand(command)
            }.launchIn(lifecycleScope)

        viewModel.flSuccessSnackbar.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { message ->
                showSuccessSnackbar(message)
            }
            .launchIn(lifecycleScope)

        viewModel.flErrorSnackbar.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { message -> showErrorSnackbar(message) }
            .launchIn(lifecycleScope)
    }

    protected fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProcessProgressDialog.createProgressDialog(requireActivity())
        }
        progressDialog?.showProgress(false)
    }

    protected fun hideProgressDialog() {
        progressDialog?.hideProgress()
    }

    protected fun showSuccessSnackbar(message: String) {
        showSnackbar(message)
    }

    protected fun showErrorSnackbar(message: String) {
        showSnackbar(message)
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val tvText = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as? TextView
        tvText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar.show()
    }

    protected fun showKeyboard() {
        val window = activity?.window ?: return
        val view = view ?: return

        WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.ime())
    }

    protected fun hideKeyboard() {
        val window = activity?.window ?: return
        val view = view ?: return

        WindowInsetsControllerCompat(window, view).hide(WindowInsetsCompat.Type.ime())
    }
}