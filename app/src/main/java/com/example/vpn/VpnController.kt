package com.example.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.example.model.VpnConfig
import com.example.model.VpnStats
import com.example.model.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnController {

    private val _status = MutableStateFlow(VpnStatus.DISCONNECTED)
    val status: StateFlow<VpnStatus> = _status.asStateFlow()

    private val _stats = MutableStateFlow(VpnStats())
    val stats: StateFlow<VpnStats> = _stats.asStateFlow()

    private val _activeConfig = MutableStateFlow<VpnConfig?>(null)
    val activeConfig: StateFlow<VpnConfig?> = _activeConfig.asStateFlow()

    fun setStatus(newStatus: VpnStatus) {
        _status.value = newStatus
    }

    fun setActiveConfig(config: VpnConfig) {
        _activeConfig.value = config
    }

    fun updateStats(
        downloadSpeed: Long,
        uploadSpeed: Long,
        totalDownload: Long,
        totalUpload: Long,
        duration: Long,
        serverLocation: String
    ) {
        _stats.value = _stats.value.copy(
            downloadSpeedBps = downloadSpeed,
            uploadSpeedBps = uploadSpeed,
            totalDownloadBytes = totalDownload,
            totalUploadBytes = totalUpload,
            durationSeconds = duration,
            serverLocation = serverLocation,
            clientIp = "192.168.1.105",
            virtualIp = "10.0.0.2"
        )
    }

    fun resetStats() {
        _stats.value = VpnStats()
    }

    fun prepareVpnIntent(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    fun startVpn(context: Context, config: VpnConfig) {
        _activeConfig.value = config
        TunnelVpnService.start(
            context = context,
            configName = config.name,
            server = config.server,
            protocol = config.protocol.displayName,
            sni = config.sni
        )
    }

    fun stopVpn(context: Context) {
        TunnelVpnService.stop(context)
    }
}
