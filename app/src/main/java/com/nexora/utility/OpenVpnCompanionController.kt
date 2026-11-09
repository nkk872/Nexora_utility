package com.nexora.utility

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback

/**
 * Controls the separately installed OpenVPN for Android engine through its
 * documented Apache-2.0 AIDL remote API. Nexora does not embed GPL engine code.
 */
class OpenVpnCompanionController(
    private val context: Context,
    private val onStatus: (state: String, message: String, level: String) -> Unit
) {
    @Volatile private var service: IOpenVPNAPIService? = null
    @Volatile var bound: Boolean = false
        private set
    private val mainHandler = Handler(Looper.getMainLooper())

    private val statusCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            mainHandler.post {
                onStatus(state.orEmpty(), message.orEmpty(), level.orEmpty())
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = IOpenVPNAPIService.Stub.asInterface(binder)
            bound = true
            runCatching { service?.registerStatusCallback(statusCallback) }
                .onFailure { onStatus("API_ERROR", it.message ?: "Could not register VPN status callback", "ERROR") }
            onStatus("READY", "OpenVPN engine is available.", "INFO")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            bound = false
            onStatus("DISCONNECTED", "OpenVPN engine service disconnected.", "ERROR")
        }
    }

    fun bind(): Boolean {
        val intent = Intent(IOpenVPNAPIService::class.java.name).setPackage(OPENVPN_PACKAGE)
        return runCatching {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }.getOrDefault(false)
    }

    fun prepareApiAccess(): Intent? =
        requireNotNull(service) { "OpenVPN engine is not connected." }.prepare(context.packageName)

    fun start(config: String) {
        require(config.contains("client")) { "The selected OpenVPN profile is invalid." }
        requireNotNull(service) { "OpenVPN engine is not connected." }.startVPN(config)
    }

    fun disconnect() {
        requireNotNull(service) { "OpenVPN engine is not connected." }.disconnect()
    }

    fun unbind() {
        val current = service
        if (current != null) runCatching { current.unregisterStatusCallback(statusCallback) }
        if (bound) runCatching { context.unbindService(connection) }
        service = null
        bound = false
    }

    companion object {
        const val OPENVPN_PACKAGE = "de.blinkt.openvpn"
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=de.blinkt.openvpn"
    }
}
