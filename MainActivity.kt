 package com.carrom.aimoverlay
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.*
import kotlin.concurrent.fixedRateTimer

class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val bot = TradingBot()
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC0A0E1A"))
            setPadding(20,20,20,20)
        }
        val status = TextView(this).apply { text="MFANAKAHTK ROBOT: SCALPING..."; setTextColor(Color.YELLOW); textSize=12f }
        val signal = TextView(this).apply { text="Signal: ANALYZING..."; setTextColor(Color.CYAN); textSize=18f; setPadding(0,10,0,10) }
        val broker = TextView(this).apply { text="Broker: ${BrokerManager.getFastest().name}"; setTextColor(Color.WHITE); textSize=10f }
        layout.addView(status); layout.addView(signal); layout.addView(broker)
        fixedRateTimer("scalp", false, 0L, 1000L) {
            val fakeCandles = List(20) { Math.random()*1.2 }
            val sig = bot.analyze(fakeCandles)
            signal.post {
                signal.text = "Signal: $sig"
                signal.setTextColor(when(sig){ "BUY"->Color.GREEN; "SELL"->Color.RED; else->Color.CYAN })
            }
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x=20; y=200 }
        windowManager?.addView(layout, params)
        overlayView = layout
        bot.isRunning = true
    }
    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager?.removeView(it) }
        bot.isRunning = false
    }
}
