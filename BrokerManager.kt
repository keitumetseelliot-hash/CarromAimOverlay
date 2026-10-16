package com.carrom.aimoverlay
object BrokerManager {
    data class Broker(val name:String, val spread:Double, val isFSCA:Boolean)
    val saBrokers = listOf(
        Broker("Exness ZA (FSCA)", 0.3, true),
        Broker("XM Global SA", 0.6, true),
        Broker("HFM SA", 0.0, true),
        Broker("FXTM SA", 0.5, true),
        Broker("Tickmill SA", 0.0, true),
        Broker("AvaTrade Africa", 0.9, true)
    )
    fun getFastest(): Broker = saBrokers.minByOrNull { it.spread }!!
}
