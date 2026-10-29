package com.example.data

import com.example.model.VpnLogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VpnLogManager {
    private val _logs = MutableStateFlow<List<VpnLogEntry>>(emptyList())
    val logs: StateFlow<List<VpnLogEntry>> = _logs.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun log(message: String, level: String = "INFO") {
        val entry = VpnLogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            message = message
        )
        val current = _logs.value.toMutableList()
        current.add(0, entry) // Newest at top
        if (current.size > 200) {
            _logs.value = current.take(200)
        } else {
            _logs.value = current
        }
    }

    fun clear() {
        _logs.value = emptyList()
    }

    fun formatEntry(entry: VpnLogEntry): String {
        val time = timeFormat.format(Date(entry.timestamp))
        return "[$time] [${entry.level}] ${entry.message}"
    }
}
