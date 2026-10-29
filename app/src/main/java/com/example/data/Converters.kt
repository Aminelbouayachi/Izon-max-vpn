package com.example.data

import androidx.room.TypeConverter
import com.example.model.NetworkTweak
import com.example.model.ProtocolType

class Converters {
    @TypeConverter
    fun fromProtocolType(value: ProtocolType): String = value.name

    @TypeConverter
    fun toProtocolType(value: String): ProtocolType = try {
        ProtocolType.valueOf(value)
    } catch (e: Exception) {
        ProtocolType.VMESS
    }

    @TypeConverter
    fun fromNetworkTweak(value: NetworkTweak): String = value.name

    @TypeConverter
    fun toNetworkTweak(value: String): NetworkTweak = try {
        NetworkTweak.valueOf(value)
    } catch (e: Exception) {
        NetworkTweak.GLOBAL
    }
}
