package com.nexora.utility

import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Test

class VpnGateRepositoryTest {
    @Test
    fun acceptsValidOpenVpnClientProfile() {
        val profile = "client\nremote vpn.example.com 1194\n"
        val server = sampleServer(profile)
        assertEquals(profile, VpnGateRepository.decodeOpenVpnProfile(server))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsProfileWithoutClientDirective() {
        VpnGateRepository.decodeOpenVpnProfile(sampleServer("remote vpn.example.com 1194\n"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOversizedProfile() {
        val oversized = "x".repeat(1024 * 1024 + 1)
        VpnGateRepository.decodeOpenVpnProfile(sampleServer(oversized))
    }

    private fun sampleServer(profile: String) = VpnGateServer(
        hostName = "test-host",
        ip = "192.0.2.1",
        score = 1,
        pingMs = 10,
        speedMbps = 1.0,
        countryLong = "Test",
        countryShort = "TS",
        sessions = 1,
        configBase64 = Base64.getEncoder().encodeToString(profile.toByteArray(Charsets.UTF_8))
    )
}
