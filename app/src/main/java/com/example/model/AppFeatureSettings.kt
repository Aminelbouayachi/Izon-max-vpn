package com.example.model

data class AppFeatureSettings(
    val broadcastAnnouncement: String = "⚡ تحديث جديد: ثغرة اتصالات المغرب وإنوي تعمل الآن بسرعة غير محدودة!",
    val isAnnouncementEnabled: Boolean = true,
    val isKillSwitchEnabled: Boolean = true,
    val isUdpForwardingEnabled: Boolean = true,
    val isDnsProtectionEnabled: Boolean = true,
    val isTetheringProxyEnabled: Boolean = false,
    val tetheringProxyPort: Int = 8080,
    val isTurboModeEnabled: Boolean = true,
    val isMaintenanceMode: Boolean = false,
    val encryptionKeyHash: String = "SHA256:Verified"
)
