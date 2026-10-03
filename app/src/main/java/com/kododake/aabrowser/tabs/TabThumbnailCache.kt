/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.tabs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.WebView
import java.util.concurrent.ConcurrentHashMap

object TabThumbnailCache {
    private val thumbnails = ConcurrentHashMap<Long, Bitmap>()
    private const val THUMB_WIDTH = 300
    private const val THUMB_HEIGHT = 400

    fun capture(tabId: Long, webView: WebView) {
        if (webView.width <= 0 || webView.height <= 0) return
        runCatching {
            val scale = (THUMB_WIDTH.toFloat() / webView.width).coerceAtMost(1f)
            val h = (webView.height * scale).toInt().coerceAtMost(THUMB_HEIGHT)
            val bitmap = Bitmap.createBitmap(THUMB_WIDTH, h, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            canvas.scale(scale, scale)
            webView.draw(canvas)
            val old = thumbnails.put(tabId, bitmap)
            if (old != null && old !== bitmap) {
                old.recycle()
            }
        }
    }

    fun get(tabId: Long): Bitmap? = thumbnails[tabId]

    fun remove(tabId: Long) {
        thumbnails.remove(tabId)?.recycle()
    }

    fun clear() {
        thumbnails.values.forEach { it.recycle() }
        thumbnails.clear()
    }
}
