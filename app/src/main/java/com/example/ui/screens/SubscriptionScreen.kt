package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SubscriptionState
import com.example.model.SubscriptionTier
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SubscriptionScreen(
    subscriptionState: SubscriptionState,
    onAddRewardTime: (Int) -> Unit,
    onRedeemCode: (String) -> Unit,
    onPurchasePlan: (days: Int, tier: SubscriptionTier) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var promoInput by remember { mutableStateOf("") }
    var showAdDialog by remember { mutableStateOf(false) }
    var adProgress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // VIP Virtual Pass Card
            VipMembershipCard(subscriptionState = subscriptionState)
        }

        // Method 1: Watch Ad for +2 Hours
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = NeonEmerald.copy(alpha = 0.4f)
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
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = NeonEmerald.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "شحن مجاني فوري (+2 ساعات)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "شاهد إعلان فيديو قصير لدعم التطبيق وتمديد الوقت",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            showAdDialog = true
                            adProgress = 0f
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonEmerald,
                            contentColor = Color(0xFF00381B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("watch_ad_button")
                    ) {
                        Text(text = "شحن +2h", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Method 2: Promo Code / Voucher Activation
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface,
                borderColor = NeonCyan.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تفعيل كود أو قسيمة VIP",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "أدخل كود التفعيل المقدم من المشرف أو العروض الخاصة لشحن حسابك فوراً:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoInput,
                            onValueChange = { promoInput = it },
                            placeholder = { Text("مثال: VIP30DAYS أو AMIN2026", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("promo_code_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (promoInput.isBlank()) {
                                    onShowMessage("المرجو إدخال الكود أولاً")
                                    return@Button
                                }
                                onRedeemCode(promoInput)
                                promoInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color(0xFF031625)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("redeem_code_button")
                        ) {
                            Text(text = "تفعيل", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Quick test codes pills
                    Text(text = "أكواد تجريبية جاهزة (اضغط لتجربتها):", color = TextMuted, fontSize = 10.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("VIP30DAYS", "IZONEMAX-PRO", "AMIN2026").forEach { code ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { promoInput = code }
                                    .testTag("test_code_$code"),
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Text(
                                    text = code,
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Method 3: Subscription Plans
        item {
            Text(
                text = "باقات الاشتراك الاحترافية (VIP Plans)",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            PlanCard(
                title = "الباقة الأسبوعية Turbo ⚡",
                days = 7,
                tier = SubscriptionTier.VIP_GOLD,
                price = "15 درهم / أسبوع",
                badge = "بداية سريعة",
                features = listOf(
                    "سرعة فائقة تصل إلى 100 Mbps",
                    "فتح جميع سيرفرات المغرب (IAM / Inwi / Orange)",
                    "بينغ منخفض للألعاب (PUBG / FreeFire)"
                ),
                isPopular = false,
                onSelect = { onPurchasePlan(7, SubscriptionTier.VIP_GOLD) }
            )
        }

        item {
            PlanCard(
                title = "الباقة الشهرية Gold VIP 👑",
                days = 30,
                tier = SubscriptionTier.VIP_GOLD,
                price = "40 درهم / شهر",
                badge = "الأكثر طلباً ⭐",
                features = listOf(
                    "بيانات غير محدودة (No Throttling)",
                    "سيرفرات مخصصة ببروتوكول V2Ray و SSH",
                    "تجاوز حجب المواقع وسرعة قصوى للبث المباشر",
                    "دعم فني خاص على مدار الساعة"
                ),
                isPopular = true,
                onSelect = { onPurchasePlan(30, SubscriptionTier.VIP_GOLD) }
            )
        }

        item {
            PlanCard(
                title = "الباقة السنوية Ultimate Diamond 💎",
                days = 365,
                tier = SubscriptionTier.PLATINUM_UNLIMITED,
                price = "199 درهم / سنة",
                badge = "توفير 60%",
                features = listOf(
                    "سنة كاملة بدون انقطاع أو قيود",
                    "أولوية اتصال في أوقات الذروة",
                    "تحديثات حصرية لأحدث ثغرات المشغلين",
                    "إمكانية استخدام الحساب على 3 أجهزة"
                ),
                isPopular = false,
                onSelect = { onPurchasePlan(365, SubscriptionTier.PLATINUM_UNLIMITED) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    // Simulated Ad Dialog
    if (showAdDialog) {
        LaunchedEffect(Unit) {
            for (i in 1..30) {
                delay(100)
                adProgress = i / 30f
            }
            delay(300)
            showAdDialog = false
            onAddRewardTime(2)
        }

        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = NeonEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "جاري عرض الإعلان...", color = TextPrimary, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "شكراً لدعمك خوادم iZone Max المجانية!\nسيتم شحن +2 ساعات فور اكتمال المشاهدة.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { adProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = NeonEmerald,
                        trackColor = DarkSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${((1f - adProgress) * 3).toInt() + 1} ثواني متبقية",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun VipMembershipCard(subscriptionState: SubscriptionState) {
    val gradientColors = if (subscriptionState.isVipActive) {
        listOf(Color(0xFF332002), Color(0xFF1F1705), Color(0xFF101726))
    } else {
        listOf(DarkSurfaceVariant, DarkSurface)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (subscriptionState.isVipActive) VipGold.copy(alpha = 0.6f) else DarkBorder,
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradientColors))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = if (subscriptionState.isVipActive) VipGold.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (subscriptionState.isVipActive) VipGold else DarkBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = if (subscriptionState.isVipActive) "👑" else "🛡️", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "iZone Max VIP PASS",
                                color = if (subscriptionState.isVipActive) VipGold else TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = subscriptionState.tier.title,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    CyberBadge(
                        text = if (subscriptionState.isVipActive) "مفعل (ACTIVE)" else "منتهي (EXPIRED)",
                        color = if (subscriptionState.isVipActive) NeonEmerald else Color(0xFFFF5252)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "الوقت المتبقي (Remaining Time):",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = subscriptionState.formattedTime,
                    color = if (subscriptionState.isVipActive) VipGold else TextSecondary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "حساب المستخدم: Free Net User", color = TextMuted, fontSize = 11.sp)
                    Text(text = "تشفير AES-256 GCM", color = NeonCyan, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    days: Int,
    tier: SubscriptionTier,
    price: String,
    badge: String,
    features: List<String>,
    isPopular: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isPopular) VipGold else DarkBorder
    val containerColor = if (isPopular) DarkSurfaceVariant else DarkSurface

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${days}d"),
        borderColor = borderColor,
        backgroundColor = containerColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                CyberBadge(
                    text = badge,
                    color = if (isPopular) VipGold else NeonCyan
                )
            }

            Text(
                text = price,
                color = if (isPopular) VipGold else NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))

            features.forEach { feat ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feat, color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("subscribe_button_${days}d"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) VipGold else DarkSurfaceVariant,
                    contentColor = if (isPopular) Color(0xFF3F2E00) else NeonCyan
                ),
                shape = RoundedCornerShape(10.dp),
                border = if (!isPopular) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)) else null
            ) {
                Text(text = "اختيار هذه الباقة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
