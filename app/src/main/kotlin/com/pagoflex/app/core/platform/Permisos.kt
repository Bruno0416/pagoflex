package com.pagoflex.app.core.platform

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object Permisos {
    const val CAMARA: String = Manifest.permission.CAMERA

    // ---------- verificar permiso ----------
    fun tieneCamara(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, CAMARA) == PackageManager.PERMISSION_GRANTED

    // ---------- abrir configuración ----------
    fun abrirAjustes(context: Context) {
        // 1. abrir los ajustes de permisos de esta aplicación
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}
