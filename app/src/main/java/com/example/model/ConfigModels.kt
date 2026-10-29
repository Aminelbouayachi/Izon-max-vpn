package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProtocolType(val displayName: String, val category: String) {
    VMESS("VMess (V2Ray)", "V2Ray"),
    VLESS("VLess (V2Ray)", "V2Ray"),
    TROJAN("Trojan (V2Ray)", "V2Ray"),
    SHADOWSOCKS("Shadowsocks", "V2Ray"),
    SSH_DIRECT("SSH Direct", "SSH"),
    SSH_SSL("SSH + SSL/TLS", "SSH"),
    SSH_WS("SSH + WebSocket", "SSH");

    val isV2Ray: Boolean get() = category == "V2Ray"
    val isSsh: Boolean get() = category == "SSH"
}

enum class NetworkTweak(val displayName: String, val operatorName: String) {
    IAM("Maroc Telecom (IAM)", "IAM"),
    INWI("Inwi Unlimited", "INWI"),
    ORANGE("Orange Fast Bug", "ORANGE"),
    GLOBAL("Global High Speed", "GLOBAL"),
    CUSTOM("Custom Tweak", "CUSTOM")
}

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    AUTHENTICATING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

@Entity(tableName = "vpn_configs")
data class VpnConfig(
    @PrimaryKey
    val id: String,
    val name: String,
    val protocol: ProtocolType,
    val server: String,
    val port: Int,
    val uuidOrUser: String,
    val passwordOrKey: String = "",
    val sni: String = "",
    val wsPath: String = "",
    val payload: String = "",
    val networkTweak: NetworkTweak = NetworkTweak.GLOBAL,
    val isVip: Boolean = false,
    val flagEmoji: String = "🌐",
    val countryName: String = "Server",
    val pingMs: Int = 45,
    val isCustom: Boolean = false,
    val isFromAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class VpnStats(
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val totalDownloadBytes: Long = 0L,
    val totalUploadBytes: Long = 0L,
    val durationSeconds: Long = 0L,
    val pingMs: Int = 0,
    val clientIp: String = "192.168.1.105",
    val virtualIp: String = "10.0.0.2",
    val serverLocation: String = "Morocco (Protected)",
    val encryptionType: String = "AES-256-GCM / TLS 1.3"
)

data class VpnLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: String = "INFO",
    val message: String
)

enum class SubscriptionTier(val title: String, val badgeColorHex: Long) {
    FREE("Free Tier", 0xFF64748B),
    VIP_GOLD("VIP Gold", 0xFFFFD700),
    PLATINUM_UNLIMITED("VIP Ultimate", 0xFF00E5FF)
}

data class DnsConfig(
    val id: String,
    val name: String,
    val primaryDns: String,
    val secondaryDns: String
)
