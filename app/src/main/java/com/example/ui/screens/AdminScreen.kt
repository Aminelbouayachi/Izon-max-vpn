package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BroadcastOnPersonal
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppFeatureSettings
import com.example.model.NetworkTweak
import com.example.model.ProtocolType
import com.example.model.VpnConfig
import com.example.security.CryptoUtils
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.dialogs.EditConfigDialog
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    isAuthenticated: Boolean,
    authenticatedUser: String,
    configs: List<VpnConfig>,
    appFeatureSettings: AppFeatureSettings,
    onAuthenticate: (username: String, pass: String) -> Boolean,
    onLogout: () -> Unit,
    onAddConfig: (
        name: String,
        protocol: ProtocolType,
        server: String,
        port: Int,
        uuidOrUser: String,
        passwordOrKey: String,
        sni: String,
        wsPath: String,
        payload: String,
        networkTweak: NetworkTweak,
        isVip: Boolean,
        countryName: String,
        broadcastToUsers: Boolean
    ) -> Unit,
    onUpdateConfig: (VpnConfig) -> Unit,
    onUpdateFeatures: (AppFeatureSettings) -> Unit,
    onGenerateEncryptedUri: (VpnConfig) -> String,
    onImportText: (String) -> Boolean,
    onDeleteConfig: (VpnConfig) -> Unit,
    onResetDefaults: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isAuthenticated) {
        SuperAdminLoginGate(
            onAuthenticate = onAuthenticate,
            modifier = modifier
        )
    } else {
        SuperAdminDashboard(
            authenticatedUser = authenticatedUser,
            configs = configs,
            appFeatureSettings = appFeatureSettings,
            onLogout = onLogout,
            onAddConfig = onAddConfig,
            onUpdateConfig = onUpdateConfig,
            onUpdateFeatures = onUpdateFeatures,
            onGenerateEncryptedUri = onGenerateEncryptedUri,
            onImportText = onImportText,
            onDeleteConfig = onDeleteConfig,
            onResetDefaults = onResetDefaults,
            onShowMessage = onShowMessage,
            modifier = modifier
        )
    }
}

@Composable
private fun SuperAdminLoginGate(
    onAuthenticate: (String, String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(90.dp),
            shape = CircleShape,
            color = NeonCyan.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "بوابة الإدارة المركزية المشفرة 🛡️",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "الوصول مقتصر على المشرف المصرح له عبر التجزئة التشفيرية الآمنة",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberBadge(text = "تشفير مشدد SHA-256 / AES-256 GCM 🔒", color = NeonEmerald)

        Spacer(modifier = Modifier.height(24.dp))

        // Username Field (Completely clean and stealth)
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                hasError = false
            },
            label = { Text("معرّف الإدارة (Admin ID)") },
            placeholder = { Text("أدخل معرّف الإدارة") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("admin_username_input"),
            colors = getTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field (Masked)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                hasError = false
            },
            label = { Text("مفتاح الأمان السري (Security Key)") },
            placeholder = { Text("••••••••••••") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = hasError,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("admin_password_input"),
            colors = getTextFieldColors()
        )

        if (hasError) {
            Text(
                text = "بيانات الدخول غير صحيحة!",
                color = Color(0xFFFF5252),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = {
                val ok = onAuthenticate(username, password)
                if (!ok) hasError = true
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .testTag("admin_login_submit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = Color(0xFF031625)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🔐 التحقق وفك القفل المشفر",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuperAdminDashboard(
    authenticatedUser: String,
    configs: List<VpnConfig>,
    appFeatureSettings: AppFeatureSettings,
    onLogout: () -> Unit,
    onAddConfig: (
        name: String,
        protocol: ProtocolType,
        server: String,
        port: Int,
        uuidOrUser: String,
        passwordOrKey: String,
        sni: String,
        wsPath: String,
        payload: String,
        networkTweak: NetworkTweak,
        isVip: Boolean,
        countryName: String,
        broadcastToUsers: Boolean
    ) -> Unit,
    onUpdateConfig: (VpnConfig) -> Unit,
    onUpdateFeatures: (AppFeatureSettings) -> Unit,
    onGenerateEncryptedUri: (VpnConfig) -> String,
    onImportText: (String) -> Boolean,
    onDeleteConfig: (VpnConfig) -> Unit,
    onResetDefaults: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var configBeingEdited by remember { mutableStateOf<VpnConfig?>(null) }

    // Form Fields
    var configName by remember { mutableStateOf("") }
    var selectedProtocol by remember { mutableStateOf(ProtocolType.VMESS) }
    var protocolDropdownExpanded by remember { mutableStateOf(false) }

    var serverHost by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("443") }
    var uuidOrUser by remember { mutableStateOf("") }
    var passwordOrKey by remember { mutableStateOf("") }
    var bugHostSni by remember { mutableStateOf("web.whatsapp.com") }
    var wsPath by remember { mutableStateOf("/izone-ws") }
    var payloadTemplate by remember { mutableStateOf("") }

    var selectedTweak by remember { mutableStateOf(NetworkTweak.IAM) }
    var tweakDropdownExpanded by remember { mutableStateOf(false) }

    var isVipOnly by remember { mutableStateOf(false) }
    var broadcastToUsers by remember { mutableStateOf(true) }

    // Dynamic Feature Settings State
    var announcementText by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.broadcastAnnouncement) }
    var announcementEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isAnnouncementEnabled) }
    var killSwitchEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isKillSwitchEnabled) }
    var udpForwardingEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isUdpForwardingEnabled) }
    var dnsProtectionEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isDnsProtectionEnabled) }
    var tetheringProxyEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isTetheringProxyEnabled) }
    var tetheringPortText by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.tetheringProxyPort.toString()) }
    var turboModeEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isTurboModeEnabled) }
    var maintenanceModeEnabled by remember(appFeatureSettings) { mutableStateOf(appFeatureSettings.isMaintenanceMode) }

    // Quick Import State
    var rawImportText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Admin Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = VipGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VipGold)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "👑", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المشرف العام (Master Administrator)",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(text = "AES-256", color = NeonEmerald)
                        }
                        Text(
                            text = "تحكم كامل بالسيرفرات والخصائص المشفرة",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Text("خروج", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Navigation Tabs: Add, Manage & Edit, App Features, Import
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("✏️ تعديل وإدارة (${configs.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🛠️ ميزات التطبيق", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("➕ إضافة يدوياً", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("📋 استيراد", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // TAB 0: EDIT AND MANAGE EXISTING CONFIGS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "كونفيغات التطبيق الحالية (${configs.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        OutlinedButton(
                            onClick = onResetDefaults,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استعادة الافتراضي", color = Color(0xFFFF5252), fontSize = 11.sp)
                        }
                    }
                }

                items(configs, key = { it.id }) { config ->
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (config.isVip) VipGold.copy(alpha = 0.5f) else DarkBorder
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = config.flagEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = config.name,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${config.protocol.displayName} • ${config.server}:${config.port}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        if (config.sni.isNotEmpty()) {
                                            Text(
                                                text = "SNI Bug: ${config.sni}",
                                                color = NeonCyan,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                if (config.isVip) {
                                    CyberBadge(text = "VIP", color = VipGold)
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))

                            // Action Buttons Row: Edit, Encrypt & Share, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // EDIT BUTTON
                                Button(
                                    onClick = { configBeingEdited = config },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .testTag("edit_config_btn_${config.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan.copy(alpha = 0.15f),
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تعديل الكونفيغ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // ENCRYPT & SHARE
                                Button(
                                    onClick = {
                                        val encLink = onGenerateEncryptedUri(config)
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Encrypted Config", encLink))
                                        onShowMessage("🔒 تم توليد ونسخ رابط النفق المشفر (AES-256)!")
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonEmerald.copy(alpha = 0.15f),
                                        contentColor = NeonEmerald
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("نسخ مشفر", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // DELETE
                                IconButton(
                                    onClick = { onDeleteConfig(config) },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: ADVANCED APP FEATURES & DYNAMIC CONTROLS (نزيد شي خاصية فالتطبيق)
                item {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = NeonPurple.copy(alpha = 0.2f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "التحكم في ميزات التطبيق الديناميكية",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "إضافة وتعديل خصائص التطبيق مع التشفير الآمن",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))

                            // Feature 1: Broadcast Announcement Banner
                            Text(
                                text = "1. شريط إعلانات وتنبيهات فورية للمستخدمين 📢",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تفعيل ظهور الشريط في الواجهة الرئيسية", color = TextSecondary, fontSize = 12.sp)
                                Switch(
                                    checked = announcementEnabled,
                                    onCheckedChange = { announcementEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                                )
                            }

                            OutlinedTextField(
                                value = announcementText,
                                onValueChange = { announcementText = it },
                                label = { Text("نص الإعلان أو التنبيه (Broadcast Message)") },
                                placeholder = { Text("مثال: ⚡ تم تحديث ثغرة اتصالات المغرب وإنوي!") },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_announcement_input"),
                                colors = getTextFieldColors()
                            )

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))

                            // Feature 2: UDP Gaming Forwarding
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تسريع ألعاب الأونلاين (UDP Gaming Mode)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("تفعيل بروتوكول UDP لمكالمات واتساب وألعاب PUBG / FreeFire", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = udpForwardingEnabled,
                                    onCheckedChange = { udpForwardingEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
                                )
                            }

                            // Feature 3: VPN Kill Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("زر الطوارئ ومنع التسريب (VPN Kill Switch)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("قطع الإنترنت فوراً عند انقطاع النفق لحماية الخصوصية", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = killSwitchEnabled,
                                    onCheckedChange = { killSwitchEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonAmber)
                                )
                            }

                            // Feature 4: Hotspot Tethering Proxy Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.WifiTethering, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("مشاركة اتصال النفق عبر الواي فاي (Hotspot Proxy)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("تمكين مشاركة النفق مع الحاسوب والهواتف الأخرى عبر البورت", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = tetheringProxyEnabled,
                                    onCheckedChange = { tetheringProxyEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple)
                                )
                            }

                            if (tetheringProxyEnabled) {
                                OutlinedTextField(
                                    value = tetheringPortText,
                                    onValueChange = { tetheringPortText = it },
                                    label = { Text("Proxy Port (منفذ المشاركة)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = getTextFieldColors()
                                )
                            }

                            // Feature 5: Turbo Mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("وضع التوربو الأقصى لجميع الخوادم (Turbo 10Gbps)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("تفعيل تحسين سرعة الحزم TCP BBR Acceleration", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = turboModeEnabled,
                                    onCheckedChange = { turboModeEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                                )
                            }

                            // Feature 6: Maintenance Mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("وضع الصيانة المؤقتة (Maintenance Mode)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("تنبيه المستخدمين بوجود تحديث للسيرفرات", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = maintenanceModeEnabled,
                                    onCheckedChange = { maintenanceModeEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF5252))
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Save & Encrypt Button
                            Button(
                                onClick = {
                                    val port = tetheringPortText.toIntOrNull() ?: 8080
                                    val updated = appFeatureSettings.copy(
                                        broadcastAnnouncement = announcementText,
                                        isAnnouncementEnabled = announcementEnabled,
                                        isKillSwitchEnabled = killSwitchEnabled,
                                        isUdpForwardingEnabled = udpForwardingEnabled,
                                        isDnsProtectionEnabled = dnsProtectionEnabled,
                                        isTetheringProxyEnabled = tetheringProxyEnabled,
                                        tetheringProxyPort = port,
                                        isTurboModeEnabled = turboModeEnabled,
                                        isMaintenanceMode = maintenanceModeEnabled
                                    )
                                    onUpdateFeatures(updated)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("save_app_features_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonEmerald,
                                    contentColor = Color(0xFF00381B)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "💾 حفظ وتشفير خصائص التطبيق بـ AES-256",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: MANUAL ADD
                item {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "إدخال كونفيغ جديد يدوي وتعميمه",
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = configName,
                                onValueChange = { configName = it },
                                label = { Text("اسم السيرفر / الكونفيغ") },
                                placeholder = { Text("مثال: 🇲🇦 IAM Free Bug VIP") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_config_name_input"),
                                colors = getTextFieldColors()
                            )

                            ExposedDropdownMenuBox(
                                expanded = protocolDropdownExpanded,
                                onExpandedChange = { protocolDropdownExpanded = !protocolDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = "${selectedProtocol.displayName} (${selectedProtocol.category})",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("البروتوكول") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = protocolDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = getTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = protocolDropdownExpanded,
                                    onDismissRequest = { protocolDropdownExpanded = false }
                                ) {
                                    ProtocolType.values().forEach { proto ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "${proto.displayName} [${proto.category}]",
                                                    color = if (proto.isV2Ray) NeonCyan else NeonPurple
                                                )
                                            },
                                            onClick = {
                                                selectedProtocol = proto
                                                protocolDropdownExpanded = false
                                                if (proto == ProtocolType.SSH_DIRECT) serverPort = "22"
                                                else if (proto == ProtocolType.SSH_WS) serverPort = "80"
                                                else serverPort = "443"
                                            }
                                        )
                                    }
                                }
                            }

                            ExposedDropdownMenuBox(
                                expanded = tweakDropdownExpanded,
                                onExpandedChange = { tweakDropdownExpanded = !tweakDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedTweak.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("المشغل (Operator)") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tweakDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = getTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = tweakDropdownExpanded,
                                    onDismissRequest = { tweakDropdownExpanded = false }
                                ) {
                                    NetworkTweak.values().forEach { tweak ->
                                        DropdownMenuItem(
                                            text = { Text(tweak.displayName) },
                                            onClick = {
                                                selectedTweak = tweak
                                                tweakDropdownExpanded = false
                                                when (tweak) {
                                                    NetworkTweak.IAM -> bugHostSni = "web.whatsapp.com"
                                                    NetworkTweak.INWI -> bugHostSni = "m.youtube.com"
                                                    NetworkTweak.ORANGE -> bugHostSni = "orange.ma"
                                                    else -> bugHostSni = "cloudflare.com"
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = serverHost,
                                    onValueChange = { serverHost = it },
                                    label = { Text("Host/IP") },
                                    placeholder = { Text("142.250.200.46") },
                                    singleLine = true,
                                    modifier = Modifier.weight(2.5f),
                                    colors = getTextFieldColors()
                                )

                                OutlinedTextField(
                                    value = serverPort,
                                    onValueChange = { serverPort = it },
                                    label = { Text("Port") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1.2f),
                                    colors = getTextFieldColors()
                                )
                            }

                            OutlinedTextField(
                                value = uuidOrUser,
                                onValueChange = { uuidOrUser = it },
                                label = { Text(if (selectedProtocol.isV2Ray) "UUID" else "SSH Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = getTextFieldColors()
                            )

                            OutlinedTextField(
                                value = passwordOrKey,
                                onValueChange = { passwordOrKey = it },
                                label = { Text(if (selectedProtocol.isV2Ray) "AlterId / Secret" else "SSH Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = getTextFieldColors()
                            )

                            OutlinedTextField(
                                value = bugHostSni,
                                onValueChange = { bugHostSni = it },
                                label = { Text("ثغرة الهوست (SNI / Bug Host)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = getTextFieldColors()
                            )

                            if (selectedProtocol == ProtocolType.VMESS || selectedProtocol == ProtocolType.VLESS || selectedProtocol == ProtocolType.SSH_WS) {
                                OutlinedTextField(
                                    value = wsPath,
                                    onValueChange = { wsPath = it },
                                    label = { Text("WebSocket Path") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = getTextFieldColors()
                                )
                            }

                            // VIP Only Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("سيرفر VIP حصري", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = isVipOnly,
                                    onCheckedChange = { isVipOnly = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = VipGold)
                                )
                            }

                            // Broadcast Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("بث وتعميم فوري لجميع المستخدمين", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = broadcastToUsers,
                                    onCheckedChange = { broadcastToUsers = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
                                )
                            }

                            Button(
                                onClick = {
                                    if (serverHost.isBlank()) {
                                        onShowMessage("المرجو إدخال عنوان السيرفر (Host/IP)")
                                        return@Button
                                    }
                                    val port = serverPort.toIntOrNull() ?: 443
                                    onAddConfig(
                                        configName.ifBlank { "${selectedTweak.operatorName} ${selectedProtocol.displayName}" },
                                        selectedProtocol,
                                        serverHost,
                                        port,
                                        uuidOrUser.ifBlank { "izone_user" },
                                        passwordOrKey,
                                        bugHostSni,
                                        wsPath,
                                        payloadTemplate,
                                        selectedTweak,
                                        isVipOnly,
                                        "المغرب (مشرف)",
                                        broadcastToUsers
                                    )
                                    configName = ""
                                    serverHost = ""
                                    uuidOrUser = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("admin_save_config_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color(0xFF031625)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("💾 حفظ وبث الكونفيغ للمستخدمين", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: QUICK IMPORT (Supports IZONE_ENC://, vmess://, vless://, ssh://)
                item {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "استيراد سريع وفك تشفير الروابط",
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "يدعم الروابط المشفرة IZONE_ENC:// وروابط vmess:// و vless:// و ssh:// المباشرة.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = rawImportText,
                                onValueChange = { rawImportText = it },
                                label = { Text("رابط الكونفيغ (Config URI)") },
                                minLines = 3,
                                maxLines = 6,
                                modifier = Modifier.fillMaxWidth(),
                                colors = getTextFieldColors()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = clipboard.primaryClip
                                        if (clip != null && clip.itemCount > 0) {
                                            rawImportText = clip.getItemAt(0).text.toString()
                                            onShowMessage("تم لصق الرابط من الحافظة! 📋")
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("لصق من الحافظة", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        if (rawImportText.isBlank()) {
                                            onShowMessage("المرجو إدخال رابط الكونفيغ أولاً")
                                            return@Button
                                        }
                                        val ok = onImportText(rawImportText)
                                        if (ok) rawImportText = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonEmerald,
                                        contentColor = Color(0xFF00381B)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("استيراد وفك التشفير", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

    // Edit Config Modal
    configBeingEdited?.let { config ->
        EditConfigDialog(
            config = config,
            onDismiss = { configBeingEdited = null },
            onSave = { updated ->
                onUpdateConfig(updated)
                configBeingEdited = null
            }
        )
    }
}

@Composable
private fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSurface,
    unfocusedContainerColor = DarkSurface,
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = DarkBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
