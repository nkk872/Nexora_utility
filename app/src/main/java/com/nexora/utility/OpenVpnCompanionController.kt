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
    @Volatile private var bindingRequested: Boolean = false
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
            val connectedService = binder?.let(IOpenVPNAPIService.Stub::asInterface)
            if (connectedService == null) {
                service = null
                bound = false
                onStatus("API_ERROR", "OpenVPN service returned an invalid connection.", "ERROR")
                return
            }
            service = connectedService
            bound = true
            val registration = runCatching {
                connectedService.registerStatusCallback(statusCallback)
            }
            registration.onSuccess {
                onStatus("READY", "OpenVPN engine is available; waiting for tunnel state.", "INFO")
            }.onFailure {
                service = null
                bound = false
                onStatus("API_ERROR", it.message ?: "Could not register VPN status callback.", "ERROR")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            bound = false
            onStatus("DISCONNECTED", "OpenVPN engine service disconnected.", "ERROR")
        }

        override fun onBindingDied(name: ComponentName?) {
            service = null
            bound = false
            bindingRequested = false
            onStatus("DISCONNECTED", "OpenVPN service binding died. Reopen the VPN screen to reconnect.", "ERROR")
            runCatching { context.unbindService(this) }
        }

        override fun onNullBinding(name: ComponentName?) {
            service = null
            bound = false
            bindingRequested = false
            onStatus("API_ERROR", "OpenVPN service returned no control interface.", "ERROR")
            runCatching { context.unbindService(this) }
        }
    }

    @Synchronized
    fun bind(): Boolean {
        if (bindingRequested) return true
        val intent = Intent(IOpenVPNAPIService::class.java.name).setPackage(OPENVPN_PACKAGE)
        val accepted = runCatching {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }.getOrDefault(false)
        bindingRequested = accepted
        if (!accepted) onStatus("NOT_INSTALLED", "OpenVPN control service could not be bound.", "ERROR")
        return accepted
    }

    fun prepareApiAccess(): Intent? =
        requireNotNull(service) { "OpenVPN engine is not connected." }.prepare(context.packageName)

    fun start(config: String) {
        require(config.length in 1..(1024 * 1024)) { "The OpenVPN profile is empty or too large." }
        require(config.contains(Regex("(?m)^\\s*client\\s*$")) &&
            config.contains(Regex("(?m)^\\s*remote\\s+\\S+\\s+\\d+"))) {
            "The selected OpenVPN client profile is invalid."
        }
        require(!config.contains("\u0000")) { "The OpenVPN profile contains invalid data." }
        requireNotNull(service) { "OpenVPN engine is not connected." }.startVPN(config)
    }

    fun disconnect() {
        requireNotNull(service) { "OpenVPN engine is not connected." }.disconnect()
    }

    @Synchronized
    fun unbind() {
        val current = service
        if (current != null) runCatching { current.unregisterStatusCallback(statusCallback) }
        if (bindingRequested) runCatching { context.unbindService(connection) }
        bindingRequested = false
        service = null
        bound = false
    }

    companion object {
        const val OPENVPN_PACKAGE = "de.blinkt.openvpn"
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=de.blinkt.openvpn"
    }
}
