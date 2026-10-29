package com.example.vpn

import com.example.model.NetworkTweak
import com.example.model.ProtocolType
import com.example.model.VpnConfig
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

object V2RaySshParser {

    private fun safeBase64Encode(data: ByteArray): String {
        return try {
            java.util.Base64.getEncoder().encodeToString(data)
        } catch (e: Throwable) {
            android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
        }
    }

    private fun safeBase64Decode(str: String): ByteArray {
        val clean = str.replace("\n", "").replace("\r", "").trim()
        return try {
            java.util.Base64.getDecoder().decode(clean)
        } catch (e: Throwable) {
            try {
                android.util.Base64.decode(clean, android.util.Base64.DEFAULT or android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE)
            } catch (ex: Throwable) {
                ByteArray(0)
            }
        }
    }

    fun parseUri(rawInput: String): VpnConfig? {
        val uri = rawInput.trim()
        if (com.example.security.CryptoUtils.isEncryptedConfigPackage(uri)) {
            val decrypted = com.example.security.CryptoUtils.decryptConfigPackage(uri)
            return parseUri(decrypted)
        }
        return try {
            when {
                uri.startsWith("vmess://", ignoreCase = true) -> parseVMess(uri)
                uri.startsWith("vless://", ignoreCase = true) -> parseVLess(uri)
                uri.startsWith("trojan://", ignoreCase = true) -> parseTrojan(uri)
                uri.startsWith("ssh://", ignoreCase = true) -> parseSsh(uri)
                uri.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(uri)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseVMess(uri: String): VpnConfig {
        val b64 = uri.substringAfter("vmess://").trim()
        val decoded = String(safeBase64Decode(b64), StandardCharsets.UTF_8)
        val json = JSONObject(decoded)

        val server = json.optString("add", "127.0.0.1")
        val port = json.optInt("port", 443)
        val uuid = json.optString("id", UUID.randomUUID().toString())
        val sni = json.optString("sni", json.optString("host", ""))
        val path = json.optString("path", "/")
        val ps = json.optString("ps", "VMess Server")
        val isTls = json.optString("tls", "").equals("tls", ignoreCase = true)

        val tweak = detectTweak(ps, sni)

        return VpnConfig(
            id = "cfg_" + UUID.randomUUID().toString().take(8),
            name = ps,
            protocol = ProtocolType.VMESS,
            server = server,
            port = port,
            uuidOrUser = uuid,
            passwordOrKey = json.optString("aid", "0"),
            sni = sni,
            wsPath = path,
            payload = if (isTls) "TLS WebSocket" else "",
            networkTweak = tweak,
            isVip = ps.contains("VIP", ignoreCase = true),
            flagEmoji = detectFlag(ps, server),
            countryName = detectCountry(ps),
            isCustom = true
        )
    }

    private fun parseVLess(uri: String): VpnConfig {
        val clean = uri.substringAfter("vless://")
        val hashPart = clean.substringAfter("#", "")
        val main = clean.substringBefore("#")
        val name = if (hashPart.isNotEmpty()) URLDecoder.decode(hashPart, "UTF-8") else "VLess Server"

        val userInfo = main.substringBefore("@")
        val remainder = main.substringAfter("@")
        val hostPort = remainder.substringBefore("?")
        val host = hostPort.substringBefore(":")
        val port = hostPort.substringAfter(":", "443").toIntOrNull() ?: 443

        val queryParams = parseQueryParams(remainder.substringAfter("?", ""))
        val sni = queryParams["sni"] ?: queryParams["host"] ?: ""
        val path = queryParams["path"] ?: "/"

        return VpnConfig(
            id = "cfg_" + UUID.randomUUID().toString().take(8),
            name = name,
            protocol = ProtocolType.VLESS,
            server = host,
            port = port,
            uuidOrUser = userInfo,
            sni = sni,
            wsPath = path,
            networkTweak = detectTweak(name, sni),
            isVip = name.contains("VIP", ignoreCase = true),
            flagEmoji = detectFlag(name, host),
            countryName = detectCountry(name),
            isCustom = true
        )
    }

    private fun parseTrojan(uri: String): VpnConfig {
        val clean = uri.substringAfter("trojan://")
        val hashPart = clean.substringAfter("#", "")
        val main = clean.substringBefore("#")
        val name = if (hashPart.isNotEmpty()) URLDecoder.decode(hashPart, "UTF-8") else "Trojan Server"

        val password = main.substringBefore("@")
        val remainder = main.substringAfter("@")
        val hostPort = remainder.substringBefore("?")
        val host = hostPort.substringBefore(":")
        val port = hostPort.substringAfter(":", "443").toIntOrNull() ?: 443

        val queryParams = parseQueryParams(remainder.substringAfter("?", ""))
        val sni = queryParams["sni"] ?: queryParams["peer"] ?: ""

        return VpnConfig(
            id = "cfg_" + UUID.randomUUID().toString().take(8),
            name = name,
            protocol = ProtocolType.TROJAN,
            server = host,
            port = port,
            uuidOrUser = password,
            passwordOrKey = password,
            sni = sni,
            networkTweak = detectTweak(name, sni),
            isVip = name.contains("VIP", ignoreCase = true),
            flagEmoji = detectFlag(name, host),
            countryName = detectCountry(name),
            isCustom = true
        )
    }

    private fun parseSsh(uri: String): VpnConfig {
        val clean = uri.substringAfter("ssh://")
        val hashPart = clean.substringAfter("#", "")
        val main = clean.substringBefore("#")
        val name = if (hashPart.isNotEmpty()) URLDecoder.decode(hashPart, "UTF-8") else "SSH Tunnel"

        val creds = main.substringBefore("@")
        val user = creds.substringBefore(":")
        val pass = creds.substringAfter(":", "")

        val remainder = main.substringAfter("@")
        val hostPort = remainder.substringBefore("?")
        val host = hostPort.substringBefore(":")
        val port = hostPort.substringAfter(":", "22").toIntOrNull() ?: 22

        val queryParams = parseQueryParams(remainder.substringAfter("?", ""))
        val sni = queryParams["sni"] ?: ""
        val payload = queryParams["payload"] ?: ""
        val mode = queryParams["mode"] ?: "ws"

        val proto = when (mode.lowercase()) {
            "ssl", "tls" -> ProtocolType.SSH_SSL
            "direct" -> ProtocolType.SSH_DIRECT
            else -> ProtocolType.SSH_WS
        }

        return VpnConfig(
            id = "cfg_" + UUID.randomUUID().toString().take(8),
            name = name,
            protocol = proto,
            server = host,
            port = port,
            uuidOrUser = user,
            passwordOrKey = pass,
            sni = sni,
            payload = payload,
            networkTweak = detectTweak(name, sni),
            isVip = name.contains("VIP", ignoreCase = true),
            flagEmoji = detectFlag(name, host),
            countryName = detectCountry(name),
            isCustom = true
        )
    }

    private fun parseShadowsocks(uri: String): VpnConfig {
        val clean = uri.substringAfter("ss://")
        val hashPart = clean.substringAfter("#", "")
        val main = clean.substringBefore("#")
        val name = if (hashPart.isNotEmpty()) URLDecoder.decode(hashPart, "UTF-8") else "Shadowsocks"

        val b64Creds = main.substringBefore("@")
        val decodedCreds = try {
            String(safeBase64Decode(b64Creds), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            b64Creds
        }

        val remainder = main.substringAfter("@")
        val host = remainder.substringBefore(":")
        val port = remainder.substringAfter(":", "8388").toIntOrNull() ?: 8388

        return VpnConfig(
            id = "cfg_" + UUID.randomUUID().toString().take(8),
            name = name,
            protocol = ProtocolType.SHADOWSOCKS,
            server = host,
            port = port,
            uuidOrUser = decodedCreds,
            passwordOrKey = decodedCreds,
            networkTweak = detectTweak(name, ""),
            isVip = name.contains("VIP", ignoreCase = true),
            flagEmoji = detectFlag(name, host),
            countryName = detectCountry(name),
            isCustom = true
        )
    }

    fun exportToUri(config: VpnConfig): String {
        return when (config.protocol) {
            ProtocolType.VMESS -> {
                val json = JSONObject().apply {
                    put("v", "2")
                    put("ps", config.name)
                    put("add", config.server)
                    put("port", config.port)
                    put("id", config.uuidOrUser)
                    put("aid", config.passwordOrKey.ifEmpty { "0" })
                    put("net", if (config.wsPath.isNotEmpty()) "ws" else "tcp")
                    put("type", "none")
                    put("host", config.sni)
                    put("path", config.wsPath)
                    put("tls", if (config.sni.isNotEmpty()) "tls" else "")
                }
                val encoded = safeBase64Encode(json.toString().toByteArray(StandardCharsets.UTF_8))
                "vmess://$encoded"
            }
            ProtocolType.VLESS -> {
                val encodedName = URLEncoder.encode(config.name, "UTF-8")
                "vless://${config.uuidOrUser}@${config.server}:${config.port}?type=ws&security=tls&sni=${config.sni}&path=${config.wsPath}#$encodedName"
            }
            ProtocolType.TROJAN -> {
                val encodedName = URLEncoder.encode(config.name, "UTF-8")
                "trojan://${config.uuidOrUser}@${config.server}:${config.port}?security=tls&sni=${config.sni}#$encodedName"
            }
            ProtocolType.SSH_DIRECT, ProtocolType.SSH_SSL, ProtocolType.SSH_WS -> {
                val mode = when (config.protocol) {
                    ProtocolType.SSH_SSL -> "ssl"
                    ProtocolType.SSH_DIRECT -> "direct"
                    else -> "ws"
                }
                val encodedName = URLEncoder.encode(config.name, "UTF-8")
                val encodedPayload = URLEncoder.encode(config.payload, "UTF-8")
                "ssh://${config.uuidOrUser}:${config.passwordOrKey}@${config.server}:${config.port}?mode=$mode&sni=${config.sni}&payload=$encodedPayload#$encodedName"
            }
            ProtocolType.SHADOWSOCKS -> {
                val encodedCreds = safeBase64Encode(config.uuidOrUser.toByteArray(StandardCharsets.UTF_8))
                val encodedName = URLEncoder.encode(config.name, "UTF-8")
                "ss://$encodedCreds@${config.server}:${config.port}#$encodedName"
            }
        }
    }

    fun generateEncryptedUri(config: VpnConfig): String {
        val plainUri = exportToUri(config)
        return com.example.security.CryptoUtils.generateEncryptedConfigPackage(plainUri)
    }


    private fun parseQueryParams(query: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        query.split("&").forEach { pair ->
            val parts = pair.split("=")
            if (parts.size == 2) {
                map[parts[0]] = URLDecoder.decode(parts[1], "UTF-8")
            }
        }
        return map
    }

    private fun detectTweak(name: String, sni: String): NetworkTweak {
        val combined = "$name $sni".lowercase()
        return when {
            combined.contains("iam") || combined.contains("maroc telecom") || combined.contains("menara") -> NetworkTweak.IAM
            combined.contains("inwi") || combined.contains("win") -> NetworkTweak.INWI
            combined.contains("orange") || combined.contains("meditel") -> NetworkTweak.ORANGE
            else -> NetworkTweak.GLOBAL
        }
    }

    private fun detectFlag(name: String, host: String): String {
        val combined = "$name $host".lowercase()
        return when {
            combined.contains("morocco") || combined.contains("maroc") || combined.contains("iam") || combined.contains("inwi") || combined.contains("orange") -> "🇲🇦"
            combined.contains("france") || combined.contains("paris") || host.endsWith(".fr") -> "🇫🇷"
            combined.contains("germany") || combined.contains("frankfurt") || host.endsWith(".de") -> "🇩🇪"
            combined.contains("usa") || combined.contains("us") || combined.contains("new york") || combined.contains("america") -> "🇺🇸"
            combined.contains("uk") || combined.contains("london") || host.endsWith(".uk") -> "🇬🇧"
            combined.contains("netherlands") || combined.contains("amsterdam") || host.endsWith(".nl") -> "🇳🇱"
            combined.contains("singapore") || host.endsWith(".sg") -> "🇸🇬"
            combined.contains("spain") || host.endsWith(".es") -> "🇪🇸"
            combined.contains("canada") || host.endsWith(".ca") -> "🇨🇦"
            else -> "🌐"
        }
    }

    private fun detectCountry(name: String): String {
        val n = name.lowercase()
        return when {
            n.contains("morocco") || n.contains("maroc") || n.contains("iam") || n.contains("inwi") || n.contains("orange") -> "Morocco"
            n.contains("france") || n.contains("paris") -> "France"
            n.contains("germany") || n.contains("frankfurt") -> "Germany"
            n.contains("usa") || n.contains("us") -> "United States"
            n.contains("uk") || n.contains("london") -> "United Kingdom"
            n.contains("netherlands") || n.contains("amsterdam") -> "Netherlands"
            n.contains("singapore") -> "Singapore"
            else -> "Global Server"
        }
    }
}
