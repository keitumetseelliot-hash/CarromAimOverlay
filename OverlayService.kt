package com.keitumetseelliot.carromaimoverlay

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.provider.Settings
import android.view.*
import androidx.core.app.NotificationCompat

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var aimView: AimOverlayView
    private var projection: MediaProjection? = null
    private var detector: ScreenDetector? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(42, NotificationCompat.Builder(this, "aim")
            .setContentTitle("Carrom Aim Overlay")
            .setContentText("Visual aiming guide is active")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build())

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        aimView = AimOverlayView(this)

        val type = if (Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(aimView, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (projection == null && intent != null) {
            val resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
            val data = intent.getParcelableExtra<Intent>("data")
            if (data != null) {
                val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                projection = mgr.getMediaProjection(resultCode, data)
                detector = ScreenDetector(this, projection!!) { guide ->
                    aimView.updateGuide(guide)
                }
                detector?.start()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        detector?.stop()
        projection?.stop()
        if (::aimView.isInitialized) windowManager.removeView(aimView)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel("aim", "Aim Overlay",
                    NotificationManager.IMPORTANCE_LOW)
            )
        }
    }
}
