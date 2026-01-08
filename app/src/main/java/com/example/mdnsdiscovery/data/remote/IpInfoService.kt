package com.example.mdnsdiscovery.data.remote

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object IpInfoService {

    fun getPublicIp(): String {
        val url = URL("https://api.ipify.org?format=json")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()

        val response = connection.inputStream.bufferedReader().readText()
        return JSONObject(response).getString("ip")
    }

    fun getIpInfo(ip: String): JSONObject {
        val url = URL("https://ipinfo.io/$ip/geo")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val response = connection.inputStream.bufferedReader().readText()
        return JSONObject(response)
    }
}
