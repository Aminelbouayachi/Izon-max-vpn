package com.example

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.VpnStatus
import com.example.ui.components.CyberBadge
import com.example.ui.dialogs.ImportConfigDialog
import com.example.ui.dialogs.SelectTweakDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Shield),
    SERVERS("السيرفرات", Icons.Default.Dns),
    SUBSCRIPTION("اشتراكات VIP", Icons.Default.WorkspacePremium),
    TOOLS("الأدوات", Icons.Default.Speed),
    ADMIN("المشرف", Icons.Default.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                var currentTab by remember { mutableStateOf(AppTab.HOME) }
                var showImportDialog by remember { mutableStateOf(false) }
                var showTweakDialog by remember { mutableStateOf(false) }

                // States
                val vpnStatus by viewModel.vpnStatus.collectAsStateWithLifecycle()
                val vpnStats by viewModel.vpnStats.collectAsStateWithLifecycle()
                val selectedConfig by viewModel.selectedConfig.collectAsStateWithLifecycle()
                val subscriptionState by viewModel.subscriptionState.collectAsStateWithLifecycle()
                val configs by viewModel.configs.collectAsStateWithLifecycle()
                val currentFilter by viewModel.filter.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
                val authenticatedUser by viewModel.authenticatedUser.collectAsStateWithLifecycle()
                val appFeatureSettings by viewModel.appFeatureSettings.collectAsStateWithLifecycle()
                val speedTestState by viewModel.speedTest.collectAsStateWithLifecycle()
                val selectedDns by viewModel.selectedDns.collectAsStateWithLifecycle()
                val logs by viewModel.logs.collectAsStateWithLifecycle()
                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
                val isSyncingCloud by viewModel.isSyncingCloud.collectAsStateWithLifecycle()

                // VPN Permission Launcher
                val vpnPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        viewModel.startVpnDirectly(context)
                    } else {
                        viewModel.showMessage("تم رفض إذن VPN. يرجى الموافقة للاتصال بالنفق.")
                    }
                }

                // Show Snackbar on userMessage
                LaunchedEffect(userMessage) {
                    userMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearMessage()
                    }
                }

                // Handle Back press: Return to Home tab if on another screen
                BackHandler(enabled = currentTab != AppTab.HOME) {
                    currentTab = AppTab.HOME
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(28.dp),
                                        shape = CircleShape,
                                        color = NeonCyan.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "⚡", fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "iZone Max",
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "V2Ray & SSH", color = NeonCyan)
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = DarkBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_navigation_bar"),
                            containerColor = DarkSurface,
                            tonalElevation = 8.dp
                        ) {
                            AppTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan.copy(alpha = 0.18f),
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                AppTab.HOME -> {
                                    HomeScreen(
                                        vpnStatus = vpnStatus,
                                        vpnStats = vpnStats,
                                        selectedConfig = selectedConfig,
                                        vipTimeFormatted = subscriptionState.formattedTime,
                                        isVipActive = subscriptionState.isVipActive,
                                        isSyncingCloud = isSyncingCloud,
                                        broadcastAnnouncement = appFeatureSettings.broadcastAnnouncement,
                                        isAnnouncementEnabled = appFeatureSettings.isAnnouncementEnabled,
                                        onToggleVpn = {
                                            viewModel.toggleVpnConnection(context) {
                                                val prepareIntent = VpnService.prepare(context)
                                                if (prepareIntent != null) {
                                                    vpnPermissionLauncher.launch(prepareIntent)
                                                }
                                            }
                                        },
                                        onSelectServerClick = { currentTab = AppTab.SERVERS },
                                        onSelectTweakClick = { showTweakDialog = true },
                                        onAddTimeClick = { currentTab = AppTab.SUBSCRIPTION },
                                        onSyncCloudClick = { viewModel.syncConfigsFromCloud() },
                                        onOpenLogsClick = { currentTab = AppTab.TOOLS },
                                        onOpenAdminClick = { currentTab = AppTab.ADMIN }
                                    )
                                }
                                AppTab.SERVERS -> {
                                    ServersScreen(
                                        configs = configs,
                                        selectedConfig = selectedConfig,
                                        currentFilter = currentFilter,
                                        searchQuery = searchQuery,
                                        onFilterChange = { viewModel.setFilter(it) },
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        onSelectConfig = { config ->
                                            viewModel.selectConfig(config)
                                            viewModel.showMessage("تم اختيار: ${config.name}")
                                        },
                                        onPingConfig = { viewModel.pingConfig(it) },
                                        onPingAll = { viewModel.pingAllConfigs() },
                                        onDeleteConfig = { viewModel.deleteConfig(it) },
                                        onAddConfigClick = { showImportDialog = true },
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                                AppTab.SUBSCRIPTION -> {
                                    SubscriptionScreen(
                                        subscriptionState = subscriptionState,
                                        onAddRewardTime = { viewModel.addRewardTime(it) },
                                        onRedeemCode = { viewModel.redeemPromoCode(it) },
                                        onPurchasePlan = { days, tier -> viewModel.purchasePlan(days, tier) },
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                                AppTab.TOOLS -> {
                                    ToolsScreen(
                                        speedTestState = speedTestState,
                                        onRunSpeedTest = { viewModel.runSpeedTest() },
                                        dnsList = viewModel.dnsList,
                                        selectedDns = selectedDns,
                                        onSelectDns = { viewModel.selectDns(it) },
                                        logs = logs,
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                                AppTab.ADMIN -> {
                                    AdminScreen(
                                        isAuthenticated = isAdminAuthenticated,
                                        authenticatedUser = authenticatedUser,
                                        configs = configs,
                                        appFeatureSettings = appFeatureSettings,
                                        onAuthenticate = { user, pass -> viewModel.authenticateSuperAdmin(user, pass) },
                                        onLogout = { viewModel.logoutAdmin() },
                                        onAddConfig = { name, proto, host, port, user, key, sni, ws, payload, tweak, vip, country, broadcast ->
                                            viewModel.adminAddConfig(
                                                name, proto, host, port, user, key, sni, ws, payload, tweak, vip, country, broadcast
                                            )
                                        },
                                        onUpdateConfig = { viewModel.updateExistingConfig(it) },
                                        onUpdateFeatures = { viewModel.updateFeatureSettings(it) },
                                        onGenerateEncryptedUri = { viewModel.generateEncryptedConfigUri(it) },
                                        onImportText = { viewModel.importConfigFromText(it) },
                                        onDeleteConfig = { viewModel.deleteConfig(it) },
                                        onResetDefaults = { viewModel.resetConfigsToDefault() },
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                            }
                        }
                    }

                    // Modals
                    if (showImportDialog) {
                        ImportConfigDialog(
                            onDismiss = { showImportDialog = false },
                            onImport = { viewModel.importConfigFromText(it) },
                            onShowMessage = { viewModel.showMessage(it) }
                        )
                    }

                    if (showTweakDialog) {
                        SelectTweakDialog(
                            configs = configs,
                            selectedConfig = selectedConfig,
                            onSelectConfig = { viewModel.selectConfig(it) },
                            onDismiss = { showTweakDialog = false }
                        )
                    }
                }
            }
        }
    }
}
