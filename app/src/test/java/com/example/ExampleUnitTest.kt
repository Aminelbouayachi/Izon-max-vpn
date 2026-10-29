package com.example

import com.example.model.NetworkTweak
import com.example.model.ProtocolType
import com.example.model.VpnConfig
import com.example.vpn.V2RaySshParser
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testVLessParserAndExport() {
        val config = VpnConfig(
            id = "test_01",
            name = "🇲🇦 IAM Bug Fast",
            protocol = ProtocolType.VLESS,
            server = "142.250.200.46",
            port = 443,
            uuidOrUser = "12345678-1234-1234-1234-123456789abc",
            sni = "web.whatsapp.com",
            wsPath = "/izone-ws",
            networkTweak = NetworkTweak.IAM,
            isVip = false
        )

        val uri = V2RaySshParser.exportToUri(config)
        assertTrue(uri.startsWith("vless://"))
        assertTrue(uri.contains("web.whatsapp.com"))

        val parsed = V2RaySshParser.parseUri(uri)
        assertNotNull(parsed)
        assertEquals(ProtocolType.VLESS, parsed?.protocol)
        assertEquals("142.250.200.46", parsed?.server)
        assertEquals(443, parsed?.port)
        assertEquals("web.whatsapp.com", parsed?.sni)
    }

    @Test
    fun testSshParserAndExport() {
        val config = VpnConfig(
            id = "test_ssh_02",
            name = "Inwi SSH Tunnel",
            protocol = ProtocolType.SSH_WS,
            server = "inwi.proxy.ma",
            port = 80,
            uuidOrUser = "izone_user",
            passwordOrKey = "secretpass",
            sni = "m.youtube.com",
            payload = "GET / HTTP/1.1[crlf]Host: m.youtube.com[crlf][crlf]",
            networkTweak = NetworkTweak.INWI
        )

        val uri = V2RaySshParser.exportToUri(config)
        assertTrue(uri.startsWith("ssh://"))

        val parsed = V2RaySshParser.parseUri(uri)
        assertNotNull(parsed)
        assertEquals(ProtocolType.SSH_WS, parsed?.protocol)
        assertEquals("inwi.proxy.ma", parsed?.server)
        assertEquals(80, parsed?.port)
        assertEquals("izone_user", parsed?.uuidOrUser)
    }

    @Test
    fun testVMessExport() {
        val config = VpnConfig(
            id = "test_vmess_03",
            name = "Paris VMess",
            protocol = ProtocolType.VMESS,
            server = "fr.turbo.net",
            port = 443,
            uuidOrUser = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            sni = "fr.turbo.net",
            wsPath = "/vmess"
        )

        val uri = V2RaySshParser.exportToUri(config)
        assertTrue(uri.startsWith("vmess://"))
        val parsed = V2RaySshParser.parseUri(uri)
        assertNotNull(parsed)
        assertEquals(ProtocolType.VMESS, parsed?.protocol)
        assertEquals("fr.turbo.net", parsed?.server)
    }

    @Test
    fun testSuperAdminAuth() {
        val u = String(charArrayOf('A','m','i','n','e','l','b','o','u','a','y','a','c','h','i'))
        val p = String(charArrayOf('1','0','8','8','1','2','2','6','a','A','@'))
        val valid = com.example.security.CryptoUtils.verifySuperAdmin(u, p)
        assertTrue(valid)

        val invalidUser = com.example.security.CryptoUtils.verifySuperAdmin("intruder", p)
        assertFalse(invalidUser)

        val invalidPass = com.example.security.CryptoUtils.verifySuperAdmin(u, "invalid_pass")
        assertFalse(invalidPass)
    }

    @Test
    fun testEncryptionAndDecryption() {
        val original = "vmess://eyJhZGQiOiJtYS1jYXMuaXpvbmUtdHVubmVsLm5ldCIsImFpZCI6IjAiLCJob3N0Ijoid2ViLndoYXRzYXBwLmNvbSIsImlkIjoiZTZjNDlhMzctMDEyOS00MTEyLTkyYTEtZmEwODEyYmQ4NDAxIiwibmV0Ijoid3MiLCJwYXRoIjoiL2l6b25lLXdzIiwicG9ydCI6NDQzLCJwcyI6IlRFU1QiLCJ0bHMiOiJ0bHMiLCJ2IjoiMiJ9"
        val encrypted = com.example.security.CryptoUtils.encrypt(original)
        assertNotEquals(original, encrypted)

        val decrypted = com.example.security.CryptoUtils.decrypt(encrypted)
        assertEquals(original, decrypted)

        val pkg = com.example.security.CryptoUtils.generateEncryptedConfigPackage(original)
        assertTrue(com.example.security.CryptoUtils.isEncryptedConfigPackage(pkg))
        val unpacked = com.example.security.CryptoUtils.decryptConfigPackage(pkg)
        assertEquals(original, unpacked)
    }
}

