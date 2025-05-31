package com.amirmuhsin.listinghelper.core_views.progress

import android.content.Context
import android.os.Bundle
import com.amirmuhsin.listinghelper.R
import com.tasomaniac.android.widget.DelayedProgressDialog

class ProcessProgressDialog(context: Context, theme: Int): DelayedProgressDialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_processed)
    }

    fun showProgress(cancelable: Boolean = true) {
        if (isShowing) return

        setCancelable(cancelable)
        show()
    }

    fun hideProgress() {
        cancel()
    }

    companion object {

        private const val MIN_DELAY = 300
        private const val MIN_SHOW_TIME = 300

        fun createProgressDialog(context: Context, theme: Int = R.style.ProgressBarTheme): ProcessProgressDialog {
            return ProcessProgressDialog(context, theme).apply {
                setMinDelay(MIN_DELAY)
                setMinShowTime(MIN_SHOW_TIME)
            }
        }
    }
}