package com.amirmuhsin.listinghelper.core_views.base.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.amirmuhsin.listinghelper.core_views.doOnApplyWindowInsets
import com.amirmuhsin.listinghelper.core_views.progress.ProcessProgressDialog
import com.amirmuhsin.listinghelper.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseBottomSheetDialog<VB: ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
): BottomSheetDialogFragment() {

    protected val binding get() = _binding!!
    private var _binding: VB? = null

    private var progressDialog: ProcessProgressDialog? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object: BottomSheetDialog(requireContext(), theme) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                makeBottomSheetParentLayoutsFullScreen(window, this)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return _binding?.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProcessProgressDialog.createProgressDialog(requireActivity())
        moveLayoutAboveNavigationBar()

        assignObjects()
        setListeners()
        prepareUI()
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

    protected fun showProgressDialog() {
        progressDialog?.show()
    }

    protected fun hideProgressDialog() {
        progressDialog?.hide()
    }

    protected fun showSuccessSnackbar(message: String) {
        showSnackbar(message, R.color.snackbar_success_bg)
    }

    protected fun showErrorSnackbar(message: String) {
        showSnackbar(message, R.color.snackbar_error_bg)
    }

    private fun showSnackbar(message: String, color: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    // non-full screen bottom sheet related code, it gonna break the full screen bottom sheet dialog
    private fun moveLayoutAboveNavigationBar() {
        binding.root.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
                ).bottom
            )
        }
    }

    // non-full screen bottom sheet related code, it is gonna break the full screen bottom sheet dialog
    private fun makeBottomSheetParentLayoutsFullScreen(window: Window?, dialog: BottomSheetDialog) {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }

        dialog.findViewById<View>(com.google.android.material.R.id.container)?.apply {
            fitsSystemWindows = false
            doOnApplyWindowInsets { insetView, windowInsets, initialPadding, initialMargins ->
                insetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(
                        top = initialMargins.top + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                    )
                }
            }

        }
        dialog.findViewById<View>(com.google.android.material.R.id.coordinator)?.fitsSystemWindows = false
    }

}