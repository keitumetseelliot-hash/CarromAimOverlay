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
            setPadding(30,30,30,30)
        }
        val title = TextView(this).apply { 
            text="MFANAKAHTK ROBOT ACTIVE"; 
            setTextColor(Color.parseColor("#FFD700")); 
            textSize=14f 
        }
        val status = TextView(this).apply { 
            text="SCALPING..."; 
            setTextColor(Color.YELLOW); 
            textSize=12f 
        }
        val signal = TextView(this).apply { 
            text="Signal: ANALYZING MT4/MT5..."; 
            setTextColor(Color.CYAN); 
            textSize=18f; 
            setPadding(0,20,0,10) 
        }
        val broker = TextView(this).apply { 
            text="Broker: ${BrokerManager.getFastest().name} | Fast <100ms"; 
            setTextColor(Color.WHITE); 
            textSize=10f 
        }
        val pair = TextView(this).apply { 
            text="EUR/USD M1 | Spread: 0.3 | Auto Scalp ON"; 
            setTextColor(Color.LTGRAY); 
            textSize=10f 
        }
        val buyBtn = Button(this).apply { 
            text="AUTO BUY SIGNAL"; 
            setBackgroundColor(Color.parseColor("#00C853")); 
            setTextColor(Color.WHITE)
        }
        val sellBtn = Button(this).apply { 
            text="AUTO SELL SIGNAL"; 
            setBackgroundColor(Color.parseColor("#D50000")); 
            setTextColor(Color.WHITE)
        }
        layout.addView(title); layout.addView(status); 
        layout.addView(signal); layout.addView(broker); 
        layout.addView(pair); layout.addView(buyBtn); layout.addView(sellBtn)
        
        fixedRateTimer("scalp", false, 0L, 1000L) {
            val fakeCandles = List(20) { Math.random()*1.2 }
            val sig = bot.analyze(fakeCandles)
            signal.post {
               
