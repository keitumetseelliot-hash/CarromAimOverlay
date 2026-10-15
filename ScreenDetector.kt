package com.keitumetseelliot.carromaimoverlay

import android.content.Context
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Lightweight, dependency-free prototype detector.
 *
 * It samples the captured screen and looks for a large, roughly square
 * warm/wood-colored region. Within that region it searches for dark/light
 * circular blobs. This is deliberately conservative: it only produces
 * a guide when the geometry is reasonably clear.
 *
 * No touch injection or game control is performed.
 */
class ScreenDetector(
    private val context: Context,
    private val projection: MediaProjection,
    private val onGuide: (AimGuide) -> Unit
) {
    private var reader: ImageReader? = null
    private var display: VirtualDisplay? = null
    private var thread: HandlerThread? = null
    private var handler: Handler? = null
    private var running = false
    private var lastRun = 0L

    fun start() {
        if (running) return
        running = true
        val metrics = context.resources.displayMetrics
        val w = metrics.widthPixels
        val h = metrics.heightPixels

        reader = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2)
        thread = HandlerThread("carrom-detector").also { it.start() }
        handler = Handler(thread!!.looper)

        display = projection.createVirtualDisplay(
            "CarromAimCapture", w, h, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            reader!!.surface, null, handler
        )

        reader!!.setOnImageAvailableListener({ r ->
            val now = System.currentTimeMillis()
            if (now - lastRun < 180) {
                r.acquireLatestImage()?.close()
                return@setOnImageAvailableListener
            }
            lastRun = now
            val image = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                analyze(image)
            } finally {
                image.close()
            }
        }, handler)
    }

    private fun analyze(image: android.media.Image) {
        // This prototype intentionally keeps processing small and bounded.
        val plane = image.planes[0]
        val buffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val w = image.width
        val h = image.height

        // Estimate a board rectangle by sampling a coarse grid.
        var minX = w
        var minY = h
        var maxX = 0
        var maxY = 0
        var hits = 0

        fun rgbAt(x: Int, y: Int): Int {
            val pos = y * rowStride + x * pixelStride
            if (pos + 2 >= buffer.limit()) return 0
            return (buffer.get(pos).toInt() and 255) shl 16 or
                ((buffer.get(pos + 1).toInt() and 255) shl 8) or
                (buffer.get(pos + 2).toInt() and 255)
        }

        for (y in 0 until h step max(12, h / 45)) {
            for (x in 0 until w step max(12, w / 45)) {
                val c = rgbAt(x, y)
                val r = (c shr 16) and 255
                val g = (c shr 8) and 255
                val b = c and 255
                // Broad wood/cream board heuristic.
                if (r > 90 && r > b * 1.15 && g > b * 1.05 && abs(r - g) < 120) {
                    minX = min(minX, x); maxX = max(maxX, x)
                    minY = min(minY, y); maxY = max(maxY, y)
                    hits++
                }
            }
        }

        if (hits < 30 || maxX - minX < w / 4 || maxY - minY < h / 4) {
            onGuide(AimGuide(null, null, null, null))
            return
        }

        val board = RectF(minX.toFloat(), minY.toFloat(), maxX.toFloat(), maxY.toFloat())

        // A stable visual prototype: place the striker near the lower center,
        // and aim toward the strongest dark sample inside the board.
        var darkX = board.centerX()
        var darkY = board.centerY()
        var best = 0

        val left = board.left.toInt().coerceAtLeast(0)
        val right = board.right.toInt().coerceAtMost(w - 1)
        val top = board.top.toInt().coerceAtLeast(0)
        val bottom = board.bottom.toInt().coerceAtMost(h - 1)

        for (y in top until bottom step max(8, h / 70)) {
            for (x in left until right step max(8, w / 70)) {
                val c = rgbAt(x, y)
                val r = (c shr 16) and 255
                val g = (c shr 8) and 255
                val b = c and 255
                val darkness = 765 - r - g - b
                if (darkness > best) {
                    best = darkness
                    darkX = x.toFloat()
                    darkY = y.toFloat()
                }
            }
        }

        val striker = PointF(board.centerX(), board.bottom - board.height() * 0.10f)
        val target = PointF(darkX, darkY)

        // Approximate nearest pocket from board corners.
        val pockets = arrayOf(
            PointF(board.left, board.top),
            PointF(board.right, board.top),
            PointF(board.left, board.bottom),
            PointF(board.right, board.bottom)
        )
        var pocket = pockets[0]
        var d = Float.MAX_VALUE
        for (q in pockets) {
            val dx = q.x - target.x
            val dy = q.y - target.y
            val dd = dx * dx + dy * dy
            if (dd < d) { d = dd; pocket = q }
        }

        onGuide(AimGuide(striker, target, pocket, board))
    }

    fun stop() {
        running = false
        reader?.setOnImageAvailableListener(null, null)
        display?.release()
        reader?.close()
        thread?.quitSafely()
        display = null
        reader = null
        thread = null
        handler = null
    }
}
