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
            setPadding(50, 100, 50, 100)
        }
        
        val btnPermission = Button(this).apply {
            text = "1. Grant Overlay Permission"
            setOnClickListener {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
                Toast.makeText(this@MainActivity, "Find Carrom Aim Overlay and turn ON", Toast.LENGTH_LONG).show()
            }
        }
        
        val btnStart = Button(this).apply {
            text = "2. START AIM OVERLAY"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    Toast.makeText(this@MainActivity, "Overlay Started! Open Carrom Pool", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "First grant permission!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        val btnStop = Button(this).apply {
            text = "3. STOP OVERLAY"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
                Toast.makeText(this@MainActivity, "Overlay Stopped", Toast.LENGTH_SHORT).show()
            }
        }
        
        layout.addView(btnPermission)
        layout.addView(btnStart)
        layout.addView(btnStop)
        setContentView(layout)
    }
}
