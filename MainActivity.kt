package com.carrom.aimoverlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(80, 150, 80, 80)
        }
        val btn1 = Button(this).apply {
            text = "1. Allow Overlay"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
        }
        val btn2 = Button(this).apply {
            text = "2. START OVERLAY"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    Toast.makeText(this@MainActivity, "Done! Open Carrom Pool", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this@MainActivity, "First allow permission!", Toast.LENGTH_SHORT).show()
            }
        }
        val btn3 = Button(this).apply {
            text = "3. STOP OVERLAY"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }
        layout.addView(btn1)
        layout.addView(btn2)
        layout.addView(btn3)
        setContentView(layout)
    }
}
