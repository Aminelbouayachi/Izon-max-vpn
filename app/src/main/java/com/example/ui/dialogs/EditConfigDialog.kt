package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkTweak
import com.example.model.ProtocolType
import com.example.model.VpnConfig
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditConfigDialog(
    config: VpnConfig,
    onDismiss: () -> Unit,
    onSave: (VpnConfig) -> Unit
) {
    var name by remember { mutableStateOf(config.name) }
    var protocol by remember { mutableStateOf(config.protocol) }
    var protocolDropdownExpanded by remember { mutableStateOf(false) }

    var server by remember { mutableStateOf(config.server) }
    var port by remember { mutableStateOf(config.port.toString()) }
    var uuidOrUser by remember { mutableStateOf(config.uuidOrUser) }
    var passwordOrKey by remember { mutableStateOf(config.passwordOrKey) }
    var sni by remember { mutableStateOf(config.sni) }
    var wsPath by remember { mutableStateOf(config.wsPath) }
    var payload by remember { mutableStateOf(config.payload) }

    var networkTweak by remember { mutableStateOf(config.networkTweak) }
    var tweakDropdownExpanded by remember { mutableStateOf(false) }

    var isVip by remember { mutableStateOf(config.isVip) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "تعديل إعدادات الكونفيغ ✏️",
                color = NeonCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم السيرفر") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_config_name_input"),
                    colors = editTextFieldColors()
                )

                // Protocol
                ExposedDropdownMenuBox(
                    expanded = protocolDropdownExpanded,
                    onExpandedChange = { protocolDropdownExpanded = !protocolDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${protocol.displayName} [${protocol.category}]",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("البروتوكول") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = protocolDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = editTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = protocolDropdownExpanded,
                        onDismissRequest = { protocolDropdownExpanded = false }
                    ) {
                        ProtocolType.values().forEach { proto ->
                            DropdownMenuItem(
                                text = { Text("${proto.displayName} (${proto.category})", color = if (proto.isV2Ray) NeonCyan else NeonPurple) },
                                onClick = {
                                    protocol = proto
                                    protocolDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Operator Tweak
                ExposedDropdownMenuBox(
                    expanded = tweakDropdownExpanded,
                    onExpandedChange = { tweakDropdownExpanded = !tweakDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = networkTweak.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("المشغل (Operator)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tweakDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = editTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = tweakDropdownExpanded,
                        onDismissRequest = { tweakDropdownExpanded = false }
                    ) {
                        NetworkTweak.values().forEach { tweak ->
                            DropdownMenuItem(
                                text = { Text(tweak.displayName) },
                                onClick = {
                                    networkTweak = tweak
                                    tweakDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Server & Port
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = server,
                        onValueChange = { server = it },
                        label = { Text("Server / IP") },
                        singleLine = true,
                        modifier = Modifier.weight(2.5f).testTag("edit_server_host"),
                        colors = editTextFieldColors()
                    )

                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Port") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        colors = editTextFieldColors()
                    )
                }

                // UUID / User
                OutlinedTextField(
                    value = uuidOrUser,
                    onValueChange = { uuidOrUser = it },
                    label = { Text(if (protocol.isV2Ray) "UUID" else "SSH Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editTextFieldColors()
                )

                // Password / Key
                OutlinedTextField(
                    value = passwordOrKey,
                    onValueChange = { passwordOrKey = it },
                    label = { Text(if (protocol.isV2Ray) "AlterId / Secret" else "SSH Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editTextFieldColors()
                )

                // Bug Host / SNI
                OutlinedTextField(
                    value = sni,
                    onValueChange = { sni = it },
                    label = { Text("ثغرة الهوست (SNI / Bug Host)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_sni_input"),
                    colors = editTextFieldColors()
                )

                // WS Path
                OutlinedTextField(
                    value = wsPath,
                    onValueChange = { wsPath = it },
                    label = { Text("WebSocket Path") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editTextFieldColors()
                )

                // Payload
                OutlinedTextField(
                    value = payload,
                    onValueChange = { payload = it },
                    label = { Text("Custom HTTP Payload") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editTextFieldColors()
                )

                // VIP Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سيرفر VIP حصري", color = TextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = isVip,
                        onCheckedChange = { isVip = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VipGold,
                            checkedTrackColor = VipGold.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val portNum = port.toIntOrNull() ?: config.port
                    val updated = config.copy(
                        name = name.ifBlank { config.name },
                        protocol = protocol,
                        server = server.ifBlank { config.server },
                        port = portNum,
                        uuidOrUser = uuidOrUser,
                        passwordOrKey = passwordOrKey,
                        sni = sni,
                        wsPath = wsPath,
                        payload = payload,
                        networkTweak = networkTweak,
                        isVip = isVip
                    )
                    onSave(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DarkSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_edit_config_button")
            ) {
                Text("حفظ التعديلات", fontWeight = FontWeight.Bold)
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
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSurfaceVariant,
    unfocusedContainerColor = DarkSurfaceVariant,
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = DarkBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
