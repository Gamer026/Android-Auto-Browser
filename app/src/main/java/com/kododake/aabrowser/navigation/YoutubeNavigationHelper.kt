/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.navigation

import android.net.Uri

object YoutubeNavigationHelper {
    fun normalizeForWebView(url: String): String {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return url
        val host = uri.host?.lowercase() ?: return url
        if (host == "www.youtube.com" || host == "youtube.com") {
            return uri.buildUpon()
                .authority("m.youtube.com")
                .build()
                .toString()
        }
        return url
    }
}
