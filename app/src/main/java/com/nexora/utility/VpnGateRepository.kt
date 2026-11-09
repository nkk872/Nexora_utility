package com.nexora.utility

import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import java.util.Locale

data class VpnGateServer(
    val hostName: String,
    val ip: String,
    val score: Long,
    val pingMs: Long,
    val speedMbps: Double,
    val countryLong: String,
    val countryShort: String,
    val sessions: Long,
    val configBase64: String
)

object VpnGateRepository {
    private const val DIRECTORY_URL = "https://www.vpngate.net/api/iphone/"

    fun fetchServers(): List<VpnGateServer> {
        val connection = (URL(DIRECTORY_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 20_000
            setRequestProperty("Accept", "text/csv")
            setRequestProperty("User-Agent", "NexoraUtility/0.1")
        }
        try {
            require(connection.responseCode in 200..299) { "VPN Gate returned HTTP ${connection.responseCode}" }
            val csv = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val lines = csv.lineSequence().toList()
            val headerIndex = lines.indexOfFirst { it.startsWith("#HostName,") }
            require(headerIndex >= 0) { "Unexpected VPN Gate CSV format" }
            val headers = parseCsvLine(lines[headerIndex]).map { it.removePrefix("#") }
            val indexes = headers.withIndex().associate { it.value to it.index }
            fun index(name: String) = indexes[name] ?: error("Missing VPN Gate field: $name")
            val rows = lines.drop(headerIndex + 1).mapNotNull { line ->
                if (line.isBlank() || line.startsWith("*")) return@mapNotNull null
                val fields = parseCsvLine(line)
                fun value(name: String) = fields.getOrElse(index(name)) { "" }
                val config = value("OpenVPN_ConfigData_Base64")
                if (config.isBlank()) return@mapNotNull null
                val speed = value("Speed").toDoubleOrNull()?.div(1_000_000.0) ?: 0.0
                VpnGateServer(
                    hostName = value("HostName"),
                    ip = value("IP"),
                    score = value("Score").toLongOrNull() ?: 0L,
                    pingMs = value("Ping").toLongOrNull() ?: 0L,
                    speedMbps = speed,
                    countryLong = value("CountryLong"),
                    countryShort = value("CountryShort"),
                    sessions = value("NumVpnSessions").toLongOrNull() ?: 0L,
                    configBase64 = config
                )
            }
            return rows.filter { it.hostName.isNotBlank() && it.ip.isNotBlank() && it.countryLong.isNotBlank() }
                .sortedWith(compareByDescending<VpnGateServer> { it.score }.thenBy { it.pingMs })
        } finally {
            connection.disconnect()
        }
    }

    fun decodeOpenVpnProfile(server: VpnGateServer): String =
        String(Base64.getDecoder().decode(server.configBase64), Charsets.UTF_8)

    private fun parseCsvLine(line: String): List<String> {
        val values = mutableListOf<String>()
        val field = StringBuilder()
        var quoted = false
        var index = 0
        while (index < line.length) {
            val char = line[index]
            when {
                char == '"' && quoted && index + 1 < line.length && line[index + 1] == '"' -> {
                    field.append('"')
                    index++
                }
                char == '"' -> quoted = !quoted
                char == ',' && !quoted -> {
                    values += field.toString()
                    field.setLength(0)
                }
                else -> field.append(char)
            }
            index++
        }
        values += field.toString()
        return values
    }
}
