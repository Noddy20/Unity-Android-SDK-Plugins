package com.arupakaman.pluginbasicutils.unity

import android.util.Log
import com.unity3d.player.UnityPlayer
import org.json.JSONObject

internal object UnityBridge {

    private val TAG by lazy { "BaseUnityBridge" }

    @JvmStatic
    fun sendMessage(unityObjectName: String, unityMethodName: String, msg: String) {
        kotlin.runCatching {
            UnityPlayer.UnitySendMessage(unityObjectName, unityMethodName, msg)
        }.onFailure {
            Log.e(TAG, "sendMessage Exc $it")
        }
    }

    @JvmStatic
    fun sendObject(unityObjectName: String, unityMethodName: String, msg: JSONObject) {
        kotlin.runCatching {
            UnityPlayer.UnitySendMessage(unityObjectName, unityMethodName, msg.toString())
        }.onFailure {
            Log.e(TAG, "sendMessage Exc $it")
        }
    }

}