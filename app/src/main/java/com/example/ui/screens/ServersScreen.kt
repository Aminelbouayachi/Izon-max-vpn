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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConfig
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.viewmodel.ConfigFilter
import com.example.vpn.V2RaySshParser

@Composable
fun ServersScreen(
    configs: List<VpnConfig>,
    selectedConfig: VpnConfig?,
    currentFilter: ConfigFilter,
    searchQuery: String,
    onFilterChange: (ConfigFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectConfig: (VpnConfig) -> Unit,
    onPingConfig: (VpnConfig) -> Unit,
    onPingAll: () -> Unit,
    onDeleteConfig: (VpnConfig) -> Unit,
    onAddConfigClick: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Search Bar & Ping All Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_servers_input"),
                        placeholder = { Text("بحث عن سيرفر أو ثغرة...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPingAll() }
                            .testTag("ping_all_button"),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = "فحص الكل",
                                tint = NeonAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        ConfigFilter.ALL to "الكل",
                        ConfigFilter.MOROCCO to "🇲🇦 المغرب",
                        ConfigFilter.V2RAY to "⚡ V2Ray",
                        ConfigFilter.SSH to "🛡️ SSH",
                        ConfigFilter.VIP to "👑 VIP",
                        ConfigFilter.CUSTOM to "⚙️ مخصص/الأدمن"
                    )

                    items(filters) { (filter, label) ->
                        val isSelected = currentFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurface,
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                labelColor = TextSecondary,
                                selectedLabelColor = NeonCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkBorder,
                                selectedBorderColor = NeonCyan
                            )
                        )
                    }
                }
            }

            // Results summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السيرفرات المتاحة (${configs.size})",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "انقر للاختيار والتفعيل",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Server items
            items(configs, key = { it.id }) { config ->
                val isSelected = selectedConfig?.id == config.id
                ServerItemCard(
                    config = config,
                    isSelected = isSelected,
                    onSelect = { onSelectConfig(config) },
                    onPing = { onPingConfig(config) },
                    onCopyUri = {
                        val uri = V2RaySshParser.exportToUri(config)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("iZone Config", uri))
                        onShowMessage("تم نسخ رابط الكونفيغ للحافظة! 📋")
                    },
                    onDelete = { onDeleteConfig(config) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button to Add / Import
        FloatingActionButton(
            onClick = onAddConfigClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_server_fab"),
            containerColor = NeonCyan,
            contentColor = Color(0xFF070B14)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة سيرفر")
        }
    }
}

@Composable
private fun ServerItemCard(
    config: VpnConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPing: () -> Unit,
    onCopyUri: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isSelected) NeonCyan else DarkBorder
    val bgColor = if (isSelected) DarkSurfaceVariant else DarkSurface

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("server_item_${config.id}"),
        backgroundColor = bgColor,
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flag
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = config.flagEmoji, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = config.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (config.isVip) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(text = "VIP", color = VipGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Protocol tag
                        Text(
                            text = config.protocol.displayName,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "•", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = config.countryName,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        if (config.isCustom || config.isFromAdmin) {
                            Text(text = "•", color = TextMuted, fontSize = 10.sp)
                            Text(text = "مخصص", color = NeonAmber, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Right Actions: Ping & Selected Checkmark
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ping chip (clickable to re-ping)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPing() }
                        .padding(end = 4.dp),
                    color = if (config.pingMs < 50) NeonEmerald.copy(alpha = 0.15f) else NeonAmber.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (config.pingMs < 50) NeonEmerald else NeonAmber)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${config.pingMs}ms",
                            color = if (config.pingMs < 50) NeonEmerald else NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Copy URI
                IconButton(onClick = onCopyUri, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ الرابط",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (config.isCustom || config.isFromAdmin) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "محدد",
                        tint = NeonCyan,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
