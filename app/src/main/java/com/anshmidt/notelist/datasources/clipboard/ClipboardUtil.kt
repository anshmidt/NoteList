package com.anshmidt.notelist.datasources.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardUtil(val context: Context) {

    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText(LABEL, text)
        clipboardManager.setPrimaryClip(clipData)
    }

    companion object {
        const val LABEL = "Notes"
    }

}