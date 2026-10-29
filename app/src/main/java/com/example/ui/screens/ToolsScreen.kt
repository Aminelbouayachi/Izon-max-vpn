package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnLogManager
import com.example.model.DnsConfig
import com.example.model.VpnLogEntry
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
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
import com.example.viewmodel.SpeedTestState

@Composable
fun ToolsScreen(
    speedTestState: SpeedTestState,
    onRunSpeedTest: () -> Unit,
    dnsList: List<DnsConfig>,
    selectedDns: DnsConfig,
    onSelectDns: (DnsConfig) -> Unit,
    logs: List<VpnLogEntry>,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedToolTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            TabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = DarkSurface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                        color = NeonCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedToolTab == 0,
                    onClick = { selectedToolTab = 0 },
                    text = { Text("🚀 قياس السرعة", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 1,
                    onClick = { selectedToolTab = 1 },
                    text = { Text("🌐 مغير DNS", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 2,
                    onClick = { selectedToolTab = 2 },
                    text = { Text("⚡ مولد Payload", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 3,
                    onClick = { selectedToolTab = 3 },
                    text = { Text("📜 السجل (Logs)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedToolTab) {
            0 -> {
                // SPEED TEST
                item {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkSurface,
                        borderColor = NeonCyan.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "فحص سرعة نفق iZone Max",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Speed Gauge Dial
                            Box(
                                modifier = Modifier.size(170.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { speedTestState.progress },
                                    modifier = Modifier.size(170.dp),
                                    color = NeonCyan,
                                    trackColor = DarkSurfaceVariant,
                                    strokeWidth = 10.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (speedTestState.downloadMbps > 0) String.format("%.1f", speedTestState.downloadMbps) else "0.0",
                                        color = NeonCyan,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(text = "Mbps", color = TextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = speedTestState.phase,
                                        color = NeonEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MetricColumn(
                                    label = "تحميل (Down)",
                                    value = "${String.format("%.1f", speedTestState.downloadMbps)} Mbps",
                                    color = NeonEmerald
                                )
                                MetricColumn(
                                    label = "رفع (Up)",
                                    value = "${String.format("%.1f", speedTestState.uploadMbps)} Mbps",
                                    color = NeonCyan
                                )
                                MetricColumn(
                                    label = "Latency",
                                    value = "${speedTestState.pingMs} ms",
                                    color = NeonAmber
                                )
                            }

                            Button(
                                onClick = onRunSpeedTest,
                                enabled = !speedTestState.isRunning,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("run_speed_test_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color(0xFF031625)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (speedTestState.isRunning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color(0xFF031625),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("جاري الاختبار...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("بدء اختبار السرعة الآن 🚀", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // DNS SWITCHER
                item {
                    Text(
                        text = "اختر خادم DNS لحماية الخصوصية وتجاوز الحجب:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                items(dnsList) { dns ->
                    val isSelected = selectedDns.id == dns.id
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDns(dns) }
                            .testTag("dns_item_${dns.id}"),
                        backgroundColor = if (isSelected) DarkSurfaceVariant else DarkSurface,
                        borderColor = if (isSelected) NeonCyan else DarkBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = dns.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Primary: ${dns.primaryDns} • Secondary: ${dns.secondaryDns}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // PAYLOAD GENERATOR
                item {
                    PayloadGeneratorCard(onShowMessage = onShowMessage)
                }
            }

            3 -> {
                // LIVE TUNNEL LOGS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل أحداث النفق المباشر (${logs.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row {
                            IconButton(onClick = {
                                val allText = logs.joinToString("\n") { VpnLogManager.formatEntry(it) }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("VPN Logs", allText))
                                onShowMessage("تم نسخ السجل للحافظة! 📋")
                            }) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonCyan)
                            }
                            IconButton(onClick = { VpnLogManager.clear() }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = Color(0xFFFF5252))
                            }
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF05070D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (logs.isEmpty()) {
                                item {
                                    Text(
                                        text = "> لا توجد سجلات حالياً. ابدأ الاتصال لمشاهدة تفاصيل النفق...",
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                items(logs) { entry ->
                                    val color = when (entry.level) {
                                        "ERROR" -> Color(0xFFFF5252)
                                        "WARN" -> NeonAmber
                                        else -> NeonEmerald
                                    }
                                    Text(
                                        text = VpnLogManager.formatEntry(entry),
                                        color = color,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PayloadGeneratorCard(onShowMessage: (String) -> Unit) {
    val context = LocalContext.current
    var operatorPreset by remember { mutableStateOf("IAM") }
    var bugHost by remember { mutableStateOf("web.whatsapp.com") }
    var generatedPayload by remember {
        mutableStateOf("GET / HTTP/1.1[crlf]Host: web.whatsapp.com[crlf]Upgrade: websocket[crlf][crlf]")
    }

    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "مولد ثغرات Payload لشبكات المغرب",
                color = NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("IAM" to "اتصالات المغرب", "INWI" to "إنوي", "ORANGE" to "أورنج").forEach { (op, label) ->
                    val isSelected = operatorPreset == op
                    OutlinedButton(
                        onClick = {
                            operatorPreset = op
                            when (op) {
                                "IAM" -> {
                                    bugHost = "web.whatsapp.com"
                                    generatedPayload = "GET / HTTP/1.1[crlf]Host: web.whatsapp.com[crlf]Upgrade: websocket[crlf]Connection: Keep-Alive[crlf][crlf]"
                                }
                                "INWI" -> {
                                    bugHost = "m.youtube.com"
                                    generatedPayload = "CONNECT [host_port] HTTP/1.1[crlf]Host: m.youtube.com[crlf]X-Online-Host: m.youtube.com[crlf]Upgrade: websocket[crlf][crlf]"
                                }
                                "ORANGE" -> {
                                    bugHost = "orange.ma"
                                    generatedPayload = "GET / HTTP/1.1[crlf]Host: orange.ma[crlf]Connection: Upgrade[crlf]Upgrade: websocket[crlf][crlf]"
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = label, color = if (isSelected) NeonCyan else TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            OutlinedTextField(
                value = bugHost,
                onValueChange = {
                    bugHost = it
                    generatedPayload = "GET / HTTP/1.1[crlf]Host: $it[crlf]Upgrade: websocket[crlf][crlf]"
                },
                label = { Text("هوست الثغرة (Bug Host)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = generatedPayload,
                onValueChange = { generatedPayload = it },
                label = { Text("النتيجة المجهزة (Generated HTTP Payload)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Payload", generatedPayload))
                    onShowMessage("تم نسخ Payload بنجاح! 📋")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("نسخ Payload لاستخدامه في الكونفيغ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
