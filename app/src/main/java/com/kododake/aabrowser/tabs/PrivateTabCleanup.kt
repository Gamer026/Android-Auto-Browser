/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.tabs

import android.webkit.WebView

object PrivateTabCleanup {
    fun clearAfterClose(webView: WebView) {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
    }
}
