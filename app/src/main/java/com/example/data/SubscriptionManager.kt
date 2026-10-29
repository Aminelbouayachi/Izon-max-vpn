package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.SubscriptionTier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubscriptionState(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val remainingMillis: Long = 0L,
    val isVipActive: Boolean = false,
    val formattedTime: String = "00:00:00",
    val promoHistory: List<String> = emptyList()
)

class SubscriptionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("izone_subscription_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(SubscriptionState())
    val state: StateFlow<SubscriptionState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        // Initialize default free trial (4 hours) if new install
        if (!prefs.contains(KEY_EXPIRY)) {
            val initialExpiry = System.currentTimeMillis() + (4 * 3600 * 1000L)
            prefs.edit()
                .putLong(KEY_EXPIRY, initialExpiry)
                .putString(KEY_TIER, SubscriptionTier.VIP_GOLD.name)
                .apply()
        }

        updateState()

        // Background ticker that ticks every second to update remaining time
        scope.launch {
            while (true) {
                delay(1000)
                updateState()
            }
        }
    }

    private fun updateState() {
        val expiryTime = prefs.getLong(KEY_EXPIRY, 0L)
        val now = System.currentTimeMillis()
        val remaining = (expiryTime - now).coerceAtLeast(0L)
        val isVip = remaining > 0

        val tier = if (isVip) {
            val tierStr = prefs.getString(KEY_TIER, SubscriptionTier.VIP_GOLD.name)
            try { SubscriptionTier.valueOf(tierStr ?: SubscriptionTier.VIP_GOLD.name) }
            catch (e: Exception) { SubscriptionTier.VIP_GOLD }
        } else {
            SubscriptionTier.FREE
        }

        val formatted = formatRemainingTime(remaining)
        val history = prefs.getStringSet(KEY_HISTORY, emptySet())?.toList() ?: emptyList()

        _state.value = SubscriptionState(
            tier = tier,
            remainingMillis = remaining,
            isVipActive = isVip,
            formattedTime = formatted,
            promoHistory = history
        )
    }

    fun addTime(additionalMillis: Long, newTier: SubscriptionTier = SubscriptionTier.VIP_GOLD) {
        val now = System.currentTimeMillis()
        val currentExpiry = prefs.getLong(KEY_EXPIRY, now).coerceAtLeast(now)
        val newExpiry = currentExpiry + additionalMillis

        prefs.edit()
            .putLong(KEY_EXPIRY, newExpiry)
            .putString(KEY_TIER, newTier.name)
            .apply()

        updateState()
    }

    fun redeemCode(code: String): Result<String> {
        val clean = code.trim().uppercase()
        val history = prefs.getStringSet(KEY_HISTORY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (history.contains(clean)) {
            return Result.failure(Exception("هذا الكود تم استخدامه مسبقاً (Code already used)"))
        }

        return when {
            clean == "VIP30DAYS" -> {
                addTime(30L * 24 * 3600 * 1000L, SubscriptionTier.VIP_GOLD)
                recordCode(history, clean)
                Result.success("تم تفعيل 30 يوم VIP بنجاح! 🚀")
            }
            clean == "IZONEMAX-PRO" -> {
                addTime(60L * 24 * 3600 * 1000L, SubscriptionTier.PLATINUM_UNLIMITED)
                recordCode(history, clean)
                Result.success("تم تفعيل حساب Ultimate Pro لمدة 60 يوم! 💎")
            }
            clean == "AMIN2026" || clean == "ADMIN" -> {
                addTime(365L * 24 * 3600 * 1000L, SubscriptionTier.PLATINUM_UNLIMITED)
                recordCode(history, clean)
                Result.success("مرحباً بالمشرف! تم تفعيل اشتراك سنوي غير محدود! 👑")
            }
            clean == "SPEED999" -> {
                addTime(15L * 24 * 3600 * 1000L, SubscriptionTier.VIP_GOLD)
                recordCode(history, clean)
                Result.success("تم شحن 15 يوم توربو فائق السرعة! ⚡")
            }
            clean.startsWith("VIP") -> {
                addTime(7L * 24 * 3600 * 1000L, SubscriptionTier.VIP_GOLD)
                recordCode(history, clean)
                Result.success("تم تفعيل 7 أيام VIP بنجاح! 🎉")
            }
            else -> {
                Result.failure(Exception("الكود غير صحيح. جرب مثلاً: VIP30DAYS أو AMIN2026"))
            }
        }
    }

    private fun recordCode(history: MutableSet<String>, code: String) {
        history.add(code)
        prefs.edit().putStringSet(KEY_HISTORY, history).apply()
    }

    private fun formatRemainingTime(millis: Long): String {
        if (millis <= 0) return "00:00:00"
        val totalSec = millis / 1000
        val days = totalSec / 86400
        val hours = (totalSec % 86400) / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60

        return if (days > 0) {
            String.format("%d يوم %02d:%02d:%02d", days, hours, minutes, seconds)
        } else {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    companion object {
        private const val KEY_EXPIRY = "sub_expiry_timestamp"
        private const val KEY_TIER = "sub_tier"
        private const val KEY_HISTORY = "sub_promo_history"
    }
}
