package com.carrom.aimoverlay
class TradingBot {
    var isRunning = false
    fun analyze(candles: List<Double>): String {
        if (candles.size < 14) return "WAIT"
        val ema7 = candles.takeLast(7).average()
        val ema14 = candles.takeLast(14).average()
        return if (ema7 > ema14) "BUY" else "SELL"
    }
}
