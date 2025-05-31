package com.amirmuhsin.listinghelper.core_views.confirm_dialog

import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.core_views.base.ui.BaseBottomSheetDialog
import com.amirmuhsin.listinghelper.databinding.DialogConfirmBinding

class ConfirmDialog: BaseBottomSheetDialog<DialogConfirmBinding>(
    DialogConfirmBinding::inflate
) {

    private lateinit var title: String
    private lateinit var subtitle: String
    private lateinit var confirmText: String
    private lateinit var cancelText: String
    private lateinit var onConfirm: () -> Unit

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun prepareUI() {
        binding.tvTitle.text = title
        binding.tvSubtitle.text = subtitle
        binding.tvSubtitle.isVisible = subtitle.isNotEmpty()
        binding.btnPrimary.text = confirmText
        binding.btnSecondary.text = cancelText
    }

    override fun setListeners() {
        binding.btnPrimary.setOnClickListener {
            onConfirm()
            dismiss()
        }
        binding.btnSecondary.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        fun open(
            fragmentManager: FragmentManager,
            title: String,
            subtitle: String,
            confirmText: String = "Ha",
            cancelText: String = "Yopish",
            onConfirm: () -> Unit
        ) {
            val dialog = ConfirmDialog()
            dialog.title = title
            dialog.subtitle = subtitle
            dialog.confirmText = confirmText
            dialog.cancelText = cancelText
            dialog.onConfirm = onConfirm
            dialog.show(fragmentManager, null)
        }
    }
}