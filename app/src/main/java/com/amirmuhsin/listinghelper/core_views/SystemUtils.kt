package com.amirmuhsin.listinghelper.core_views

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.inputmethod.InputMethodManager

object SystemUtils {

    fun getDeviceModel() = Build.MODEL

    fun getScreenSize(activity: Activity?): Size {
        val displayMetrics = DisplayMetrics()

        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun getOpenFileIntent(context: Context?, uriFile: Uri): Intent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(uriFile, context?.contentResolver?.getType(uriFile))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun showKeyboard(context: Context?, view: View) {
        context ?: return

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    fun hideKeyboard(context: Context?, view: View) {
        context ?: return

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun shareString(context: Context, shareTitle: String, shareBody: String, subject: String? = null) {
        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        context.startActivity(Intent.createChooser(sharingIntent, shareTitle))
    }

    fun startActivityOrWarn(ctx: Context?, intent: Intent, handleError: (e: Exception) -> Unit) {
        try {
            ctx?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            handleError(e)
        }
    }

    fun shareFileByIntent(context: Context?, uriFile: Uri, title: String) {
        val contentResolver = context?.contentResolver ?: return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = contentResolver.getType(uriFile)
            putExtra(Intent.EXTRA_STREAM, uriFile)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, title))
    }

    fun openPdfFileByIntent(context: Context, uriFile: Uri) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uriFile, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        openFileSafely(context, intent)
    }

    private fun openFileSafely(context: Context?, intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    fun openLink(context: Context?, url: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url ?: return))
        context?.startActivity(browserIntent)
    }
}