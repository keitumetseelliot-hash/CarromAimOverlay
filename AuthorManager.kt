package com.carrom.aimoverlay
object AuthManager {
    var userEmail = ""
    var userPhone = ""
    var isLoggedIn = false
    fun login(email:String, phone:String): Boolean {
        if (email.contains("@") && phone.length >= 10) {
            userEmail = email; userPhone = phone; isLoggedIn = true
            return true
        }
        return false
    }
}
