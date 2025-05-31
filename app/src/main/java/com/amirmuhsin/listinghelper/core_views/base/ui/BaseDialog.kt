package com.amirmuhsin.listinghelper.core_views.base.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.amirmuhsin.listinghelper.core_views.progress.ProcessProgressDialog

abstract class BaseDialog<VB: ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
): DialogFragment() {

    protected val binding get() = _binding!!
    private var _binding: VB? = null

    private var progressDialog: ProcessProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProcessProgressDialog.createProgressDialog(requireActivity())

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
}