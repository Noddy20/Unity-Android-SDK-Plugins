package com.arupakaman.pluginpermissionhelper.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class EnumPermStatus {

    GRANTED, NOT_GRANTED, SHOW_RATIONAL

}

fun Context.hasPermissions(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            return false
    }
    return true
}

fun Activity.getPermissionsStatus(permissions: Array<String>): EnumPermStatus {
    val hasPerm = hasPermissions(permissions)
    return if (hasPerm) EnumPermStatus.GRANTED
    else {
        if (shouldShowRational(*permissions)) EnumPermStatus.SHOW_RATIONAL else EnumPermStatus.NOT_GRANTED
    }
}

/**
 *  Request A permission
 */

fun Activity.requestPermissionsExt(permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

/**
 *  This will return true if user has denied a permission and not checked the "Never ask again" option or else false
 *  Use this to show a custom dialog explaining user why the permission is required.
 */
fun Activity.shouldShowRational(permission: String): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun Activity.shouldShowRational(vararg permissions: String): Boolean {
    permissions.forEach { permission ->
        if (shouldShowRational(permission)) return true
    }
    return false
}

/**
 *  If user has selected "Never ask again" than requestPermission won't work
 *  So send user to App settings to grant permissions manually
 */

fun Context.goToAppSettings() {
    kotlin.runCatching {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { startActivity(it) }
    }
}
