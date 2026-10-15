package com.keitumetseelliot.carromaimoverlay

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.hypot

data class AimGuide(
    val striker: PointF?,
    val target: PointF?,
    val pocket: PointF?,
    val board: RectF?
)

class AimOverlayView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    @Volatile private var guide = AimGuide(null, null, null, null)

    fun updateGuide(value: AimGuide) {
        guide = value
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val g = guide

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f

        g.board?.let {
            paint.alpha = 180
            canvas.drawRect(it, paint)
        }

        val s = g.striker
        val t = g.target
        val p = g.pocket

        if (s != null && t != null) {
            paint.alpha = 230
            canvas.drawLine(s.x, s.y, t.x, t.y, paint)
            if (p != null) canvas.drawLine(t.x, t.y, p.x, p.y, paint)

            paint.style = Paint.Style.FILL
            paint.alpha = 170
            canvas.drawCircle(s.x, s.y, 12f, paint)
            canvas.drawCircle(t.x, t.y, 10f, paint)
            p?.let { canvas.drawCircle(it.x, it.y, 13f, paint) }
        }
    }
}
