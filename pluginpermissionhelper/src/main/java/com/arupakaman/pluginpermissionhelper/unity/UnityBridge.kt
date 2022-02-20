package com.arupakaman.pluginpermissionhelper.unity

import android.util.Log
import com.unity3d.player.UnityPlayer

object UnityBridge {

    @JvmStatic
    fun sendMessage(unityObjectName: String, unityMethodName: String, msg: String) {
        kotlin.runCatching {
            Log.d("UnityBridge", "obj = $unityObjectName  method = $unityMethodName  msg = $msg")
            UnityPlayer.UnitySendMessage(unityObjectName, unityMethodName, msg)
        }.onFailure {
            Log.e("UnityBridge", "sendMessage Exc $it")
        }
    }

}