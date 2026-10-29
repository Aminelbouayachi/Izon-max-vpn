package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkTweak
import com.example.model.VpnConfig
import com.example.ui.components.CyberCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ImportConfigDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Boolean,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var inputUri by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "استيراد كونفيغ (V2Ray / SSH)",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ألصق رابط نفق من نوع vmess:// أو vless:// أو trojan:// أو ssh://",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = inputUri,
                    onValueChange = { inputUri = it },
                    placeholder = { Text("vmess://... أو vless://...", color = TextMuted, fontSize = 12.sp) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_dialog_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = clipboard.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            inputUri = clip.getItemAt(0).text.toString()
                            onShowMessage("تم لصق الرابط من الحافظة! 📋")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("لصق من الحافظة (Paste)", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputUri.isBlank()) {
                        onShowMessage("المرجو إدخال رابط الكونفيغ أولاً")
                        return@Button
                    }
                    val ok = onImport(inputUri)
                    if (ok) onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DarkSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("import_dialog_confirm_button")
            ) {
                Text("استيراد وتفعيل", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@Composable
fun SelectTweakDialog(
    configs: List<VpnConfig>,
    selectedConfig: VpnConfig?,
    onSelectConfig: (VpnConfig) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = NeonPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "اختر ثغرة المشغل (Morocco Tweaks)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "سيرفرات مجهزة بثغرات مجانية مخصصة لشبكات المغرب:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                val moroccoConfigs = configs.filter { it.networkTweak != NetworkTweak.GLOBAL || it.flagEmoji == "🇲🇦" }
                moroccoConfigs.forEach { config ->
                    val isSelected = selectedConfig?.id == config.id
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectConfig(config)
                                onDismiss()
                            },
                        backgroundColor = if (isSelected) DarkSurfaceVariant else DarkSurface,
                        borderColor = if (isSelected) NeonPurple else DarkBorder,
                        cornerRadius = 10.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = config.name,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${config.networkTweak.displayName} • SNI: ${config.sni.ifEmpty { "Direct" }}",
                                    color = NeonCyan,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NeonPurple)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("إغلاق", color = TextSecondary)
            }
        }
    )
}
