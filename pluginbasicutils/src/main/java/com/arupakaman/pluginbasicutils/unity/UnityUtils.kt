package com.arupakaman.pluginbasicutils.unity

import android.content.Context
import androidx.annotation.Keep
import com.arupakaman.pluginbasicutils.utils.*

@Keep
object UnityUtils {

    /**
     *   Show Toast
     */

    @JvmStatic
    @Keep
    fun toast(mContext: Context, msg: String){
        mContext.toast(msg)
    }

    @JvmStatic
    @Keep
    fun toastLong(mContext: Context, msg: String){
        mContext.toastLong(msg)
    }

    /**
     *   Internet Check Util
     */

    @JvmStatic
    @Keep
    fun isNetConnected(mContext: Context) = mContext.isNetConnected()

    @JvmStatic
    @Keep
    fun isWifiConnected(mContext: Context) = mContext.isWifiConnected()

    @JvmStatic
    @Keep
    fun isWifiEnabled(mContext: Context) = mContext.isWifiEnabled()

    @JvmStatic
    @Keep
    fun isHotspotEnabled(mContext: Context) = mContext.isHotspotEnabled()

    @JvmStatic
    @Keep
    fun isBluetoothEnabled(mContext: Context) = mContext.isWifiConnected()

    /**
     *   Device Info Check Util
     */

    @JvmStatic
    @Keep
    fun getAndroidDeviceId(mContext: Context) = mContext.getAndroidDeviceId()

    @JvmStatic
    @Keep
    fun isDeveloperModeEnabled(mContext: Context) = mContext.isDeveloperModeEnabled()

    @JvmStatic
    @Keep
    fun isRooted(mContext: Context) = RootChecker(mContext).isRooted()

    @JvmStatic
    @JvmOverloads
    @Keep
    fun isRootedOrHarmfulAppsInstalled(mContext: Context, list: String = ""): Boolean{
        return RootChecker(mContext).run {
            isRooted() || isPotentiallyDangerousAppsInstalled(list.split(",").map { it.trim() })
        }
    }

    @JvmStatic
    @JvmOverloads
    @Keep
    fun isGpsEnabled(mContext: Context, checkNetworkProverToo: Boolean = false) = mContext.isLocationServiceEnabled(checkNetworkProverToo)

    @JvmStatic
    @Keep
    fun getBatteryPercentage(mContext: Context) = mContext.getBatteryPercentage()

}