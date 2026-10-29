package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConfig
import com.example.model.VpnStats
import com.example.model.VpnStatus
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.PowerToggleSwitch
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold

@Composable
fun HomeScreen(
    vpnStatus: VpnStatus,
    vpnStats: VpnStats,
    selectedConfig: VpnConfig?,
    vipTimeFormatted: String,
    isVipActive: Boolean,
    isSyncingCloud: Boolean,
    broadcastAnnouncement: String = "",
    isAnnouncementEnabled: Boolean = false,
    onToggleVpn: () -> Unit,
    onSelectServerClick: () -> Unit,
    onSelectTweakClick: () -> Unit,
    onAddTimeClick: () -> Unit,
    onSyncCloudClick: () -> Unit,
    onOpenLogsClick: () -> Unit,
    onOpenAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isAnnouncementEnabled && broadcastAnnouncement.isNotBlank()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = NeonCyan.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📢", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = broadcastAnnouncement,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Top Status Bar: VIP countdown pill + Sync + Admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // VIP Pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onAddTimeClick() }
                        .testTag("vip_time_pill"),
                    color = if (isVipActive) VipGold.copy(alpha = 0.15f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isVipActive) VipGold.copy(alpha = 0.6f) else DarkBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = if (isVipActive) VipGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVipActive) vipTimeFormatted else "اشتراك مجاني (انقر للشحن)",
                            color = if (isVipActive) VipGold else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Cloud Sync Button
                    IconButton(
                        onClick = onSyncCloudClick,
                        modifier = Modifier.testTag("sync_cloud_button")
                    ) {
                        if (isSyncingCloud) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "تحديث من السحابة",
                                tint = NeonCyan
                            )
                        }
                    }

                    // Admin Console Shortcut
                    IconButton(
                        onClick = onOpenAdminClick,
                        modifier = Modifier.testTag("admin_panel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "لوحة المشرف",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // Connection State Heading
        item {
            val statusColor by animateColorAsState(
                targetValue = when (vpnStatus) {
                    VpnStatus.CONNECTED -> NeonEmerald
                    VpnStatus.CONNECTING, VpnStatus.AUTHENTICATING -> NeonAmber
                    else -> TextSecondary
                },
                label = "statusColor"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = when (vpnStatus) {
                        VpnStatus.CONNECTED -> "النفق مشفر ومتصل بنجاح"
                        VpnStatus.CONNECTING -> "جاري التحقق من الثغرة والمصافحة..."
                        VpnStatus.AUTHENTICATING -> "تأكيد بروتوكول التشفير وتوليد المفاتيح..."
                        VpnStatus.ERROR -> "تعذر الاتصال بالسيرفر المختار"
                        else -> "غير متصل - بياناتك غير محمية"
                    },
                    color = statusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (vpnStatus == VpnStatus.CONNECTED) {
                    Text(
                        text = "مدة الاتصال: ${formatDuration(vpnStats.durationSeconds)}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Giant Power Toggle Button
        item {
            PowerToggleSwitch(
                status = vpnStatus,
                onClick = onToggleVpn,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // Real-Time Speed & Telemetry Card
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = if (vpnStatus == VpnStatus.CONNECTED) NeonCyan.copy(alpha = 0.4f) else DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Download Gauge
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تحميل (Download)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = if (vpnStatus == VpnStatus.CONNECTED) formatSpeed(vpnStats.downloadSpeedBps) else "0.0 KB/s",
                            color = NeonEmerald,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(DarkBorder)
                    )

                    // Upload Gauge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "رفع (Upload)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = if (vpnStatus == VpnStatus.CONNECTED) formatSpeed(vpnStats.uploadSpeedBps) else "0.0 KB/s",
                            color = NeonCyan,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(DarkBorder)
                    )

                    // Latency / Ping
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "البينغ (Ping)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "${selectedConfig?.pingMs ?: 32} ms",
                            color = NeonAmber,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Active Server Selector Card
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectServerClick() }
                    .testTag("select_server_card"),
                backgroundColor = DarkSurfaceVariant,
                borderColor = DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Flag or Emoji
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = selectedConfig?.flagEmoji ?: "🌐",
                                    fontSize = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedConfig?.name ?: "اختر سيرفر",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (selectedConfig?.isVip == true) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "VIP", color = VipGold)
                                }
                            }
                            Text(
                                text = "${selectedConfig?.protocol?.displayName ?: "V2Ray"} • ${selectedConfig?.countryName ?: "Global"}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "تغيير",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Network Tweak / Bug Host Selector Card
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTweakClick() }
                    .testTag("select_tweak_card"),
                backgroundColor = DarkSurfaceVariant,
                borderColor = DarkBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = NeonPurple.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = NeonPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ثغرة المشغل (Network Tweak)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = selectedConfig?.networkTweak?.displayName ?: "Maroc Telecom / Inwi / Orange",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!selectedConfig?.sni.isNullOrBlank()) {
                                Text(
                                    text = "SNI Bug: ${selectedConfig?.sni}",
                                    color = NeonCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "اختيار",
                        tint = NeonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom Fast Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddTimeClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("add_time_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VipGold.copy(alpha = 0.15f),
                        contentColor = VipGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+2h شحن مجاني",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onOpenLogsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("open_logs_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سجل النفق (Logs)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatSpeed(bytesPerSec: Long): String {
    val kb = bytesPerSec / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format("%.1f MB/s", mb)
    } else {
        String.format("%.0f KB/s", kb)
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d", h, m, s)
}
