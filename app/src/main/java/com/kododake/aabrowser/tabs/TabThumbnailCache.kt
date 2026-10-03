/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.tabs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import java.util.concurrent.ConcurrentHashMap

object TabThumbnailCache {
    private val thumbnails = ConcurrentHashMap<Long, Bitmap>()
    private var startPagePlaceholder: Bitmap? = null
    private const val THUMB_WIDTH = 300
    private const val THUMB_HEIGHT = 400
    private val mainHandler = Handler(Looper.getMainLooper())

    fun capture(tabId: Long, webView: WebView) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { capture(tabId, webView) }
            return
        }
        if (webView.width <= 0 || webView.height <= 0) return
        runCatching {
            storeBitmap(tabId, renderViewToBitmap(webView))
        }
    }

    fun captureFromView(tabId: Long, view: View) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { captureFromView(tabId, view) }
            return
        }
        if (view.width <= 0 || view.height <= 0) return
        runCatching {
            storeBitmap(tabId, renderViewToBitmap(view))
        }
    }

    fun updateStartPagePlaceholder(view: View) {
        if (view.width <= 0 || view.height <= 0) return
        runCatching {
            val bitmap = renderViewToBitmap(view)
            startPagePlaceholder?.recycle()
            startPagePlaceholder = bitmap
        }
    }

    fun getStartPagePlaceholder(): Bitmap? {
        val bmp = startPagePlaceholder
        return if (bmp != null && !bmp.isRecycled) bmp else null
    }

    fun get(tabId: Long): Bitmap? {
        val bmp = thumbnails[tabId]
        return if (bmp != null && !bmp.isRecycled) bmp else null
    }

    fun getForStartPageTab(tabId: Long): Bitmap? {
        return get(tabId) ?: getStartPagePlaceholder()
    }

    fun remove(tabId: Long) {
        thumbnails.remove(tabId)?.recycle()
    }

    fun clear() {
        thumbnails.values.forEach { it.recycle() }
        thumbnails.clear()
        startPagePlaceholder?.recycle()
        startPagePlaceholder = null
    }

    private fun renderViewToBitmap(view: View): Bitmap {
        val scale = (THUMB_WIDTH.toFloat() / view.width).coerceAtMost(1f)
        val h = (view.height * scale).toInt().coerceIn(1, THUMB_HEIGHT)
        val bitmap = Bitmap.createBitmap(THUMB_WIDTH, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.scale(scale, scale)
        view.draw(canvas)
        return bitmap
    }

    private fun storeBitmap(tabId: Long, bitmap: Bitmap) {
        val old = thumbnails.put(tabId, bitmap)
        if (old != null && old !== bitmap && !old.isRecycled) {
            mainHandler.post {
                if (!thumbnails.containsValue(old)) {
                    old.recycle()
                }
            }
        }
    }
}
