package com.pagoflex.app.core.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hasher {

    private const val ITERACIONES = 600_000
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"

    fun genHash(valor: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val hash = calcularHash(valor, salt)

        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

        return "$saltBase64:$hashBase64"
    }

    fun verificar(valor: String, hashGuardado: String): Boolean {
        val partes = hashGuardado.split(":")
        return partes.size == 2 && try {
            val salt = Base64.decode(partes[0], Base64.NO_WRAP)
            val hash = Base64.decode(partes[1], Base64.NO_WRAP)

            salt.size == 16 && hash.size == 32 &&
                    MessageDigest.isEqual(hash, calcularHash(valor, salt))
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    // ---------- helper privado ----------
    private fun calcularHash(valor: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(valor.toCharArray(), salt, ITERACIONES, 256)

        return try {
            SecretKeyFactory.getInstance(ALGORITMO)
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }
}