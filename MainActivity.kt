package com.keitumetseelliot.carromaimoverlay

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    companion object { private const val CAPTURE_REQUEST = 9001 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "Carrom Aim Overlay"
            textSize = 26f
        }
        val info = TextView(this).apply {
            text = "\nThis tool draws a visual aiming guide over the screen. " +
                   "It does not tap, click, move the striker, or automate gameplay.\n\n" +
                   "1. Allow display-over-other-apps.\n" +
                   "2. Start the overlay.\n" +
                   "3. Approve Android screen capture.\n"
            textSize = 16f
        }
        val permission = Button(this).apply { text = "Allow Overlay Permission" }
        val start = Button(this).apply { text = "Start Aim Overlay" }
        val stop = Button(this).apply { text = "Stop Overlay" }

        permission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
        }
        start.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Enable overlay permission first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mgr.createScreenCaptureIntent(), CAPTURE_REQUEST)
        }
        stop.setOnClickListener { stopService(Intent(this, OverlayService::class.java)) }

        layout.addView(title)
        layout.addView(info)
        layout.addView(permission)
        layout.addView(start)
        layout.addView(stop)
        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }
            startForegroundService(intent)
            Toast.makeText(this, "Aim overlay started.", Toast.LENGTH_SHORT).show()
        }
    }
}
