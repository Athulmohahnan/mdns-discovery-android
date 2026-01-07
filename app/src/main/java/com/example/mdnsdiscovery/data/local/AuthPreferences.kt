package com.example.mdnsdiscovery.data.local

import android.content.Context

class AuthPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? =
        prefs.getString("token", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
