package com.arupakaman.pluginbasicutils.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.lang.reflect.Method


/**
 *   Toast popup
 */

fun Context.toast(mMsg: String) {
    Toast.makeText(this, mMsg, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(mMsg: String) {
    Toast.makeText(this, mMsg, Toast.LENGTH_LONG).show()
}

/**
 *   Network Connectivity Functions
 */

@Suppress("DEPRECATION")
private fun Context.getNetworkCapabilities(): Int{
    kotlin.runCatching {
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.let { conMan ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = conMan.getNetworkCapabilities(conMan.activeNetwork)?:return -1
                return when{
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2
                    else -> 0
                }
            } else {
                val capabilities = conMan.activeNetworkInfo?:return -1
                return when(capabilities.type){
                    ConnectivityManager.TYPE_WIFI -> 1
                    ConnectivityManager.TYPE_MOBILE -> 2
                    else -> 0
                }
            }
        }
    }.onFailure {
        Log.e("isNetConnected", "Exc $it")
    }
    return -1
}

fun Context.isNetConnected() = (getNetworkCapabilities() >= 0)

fun Context.isWifiConnected() = (getNetworkCapabilities() == 1)

fun Context.isWifiEnabled(): Boolean{
    kotlin.runCatching {
        val wifi = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?)?:return false
        return wifi.isWifiEnabled
    }.onFailure {
        Log.e("isWifiEnabled", "Exc $it")
    }
    return false
}

fun Context.isHotspotEnabled(): Boolean{
    /*
    public static int AP_STATE_DISABLING = 10;
    public static int AP_STATE_DISABLED = 11;
    public static int AP_STATE_ENABLING = 12;
    public static int AP_STATE_ENABLED = 13;
    public static int AP_STATE_FAILED = 14;
     */
    kotlin.runCatching {
        val wifiManager = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?)?:return false
        val method: Method = wifiManager.javaClass.getMethod("getWifiApState")
        method.isAccessible = true
        val invoke = method.invoke(wifiManager)
        return invoke?.toString() == "13"
    }.onFailure {
        Log.e("isHotspotEnabled", "Exc $it")
    }
    return false
}

@Suppress("DEPRECATION")
fun Context.isBluetoothEnabled(): Boolean{
    kotlin.runCatching {
        val isBluetoothSupported = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        if (!isBluetoothSupported) return false

        val btAdapter = (if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
            (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter else BluetoothAdapter.getDefaultAdapter())

        return (btAdapter.state == BluetoothAdapter.STATE_ON)
    }.onFailure {
        Log.e("isBluetoothEnabled", "Exc $it")
    }
    return false
}

/**
 *   Device Info Functions
 */

@SuppressLint("HardwareIds")
fun Context.getAndroidDeviceId(): String {
    kotlin.runCatching {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }.onFailure {
        Log.e("getAndroidDeviceId", "Exc $it")
    }
    return ""
}

fun Context.isDeveloperModeEnabled(): Boolean {
    applicationContext.contentResolver.runCatching {
        return (Settings.Global.getInt(this, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0)
                || (Settings.Secure.getInt(this, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0)
    }.onFailure {
        Log.e("isDeveloperModeEnabled", "Exc $it")
    }
    return false
}

fun Context.getBatteryPercentage(): Int {
    kotlin.runCatching {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, iFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level / scale.toFloat()
        Log.v("getBatteryPercentage", "Battery Percent ${batteryPct * 100}");
        return (batteryPct * 100).toInt()
    }.onFailure {
        Log.e("getBatteryPercentage", "Exc $it")
    }
    return -1
}

fun Context.isLocationServiceEnabled(netProvideToo: Boolean = false): Boolean{
    kotlin.runCatching {
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager?)?.let {manager->
            var result = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (netProvideToo){
                result = result || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
            return result
        }
    }.onFailure {
        Log.e("isLocServiceEnabled", "Exc $it")
    }
    return false
}