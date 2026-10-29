package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnStatus
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.VipGold

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = DarkBorder,
    backgroundColor: Color = DarkSurface,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun CyberBadge(
    text: String,
    color: Color = NeonCyan,
    bgColor: Color = color.copy(alpha = 0.15f),
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PowerToggleSwitch(
    status: VpnStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val (glowColor, buttonText) = when (status) {
        VpnStatus.CONNECTED -> NeonEmerald to "متصل وآمن"
        VpnStatus.CONNECTING, VpnStatus.AUTHENTICATING -> NeonAmber to "جاري الاتصال..."
        VpnStatus.ERROR -> NeonCrimson to "خطأ في الاتصال"
        else -> NeonCyan to "انقر للاتصال"
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .testTag("power_connect_button"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing glow ring
        if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) {
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.18f))
            )
            Box(
                modifier = Modifier
                    .size(165.dp)
                    .scale(pulseScale * 0.95f)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.28f))
            )
        }

        // Mid Ring Border
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DarkSurfaceVariant,
                            DarkSurface
                        )
                    )
                )
                .border(2.dp, glowColor.copy(alpha = 0.8f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner Core
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = if (status == VpnStatus.CONNECTED) {
                                listOf(NeonEmerald.copy(alpha = 0.25f), DarkSurface)
                            } else {
                                listOf(DarkSurfaceVariant, Color(0xFF070B14))
                            }
                        )
                    )
                    .border(
                        1.dp,
                        if (status == VpnStatus.CONNECTED) NeonEmerald else DarkBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "اتصال",
                    tint = glowColor,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}
