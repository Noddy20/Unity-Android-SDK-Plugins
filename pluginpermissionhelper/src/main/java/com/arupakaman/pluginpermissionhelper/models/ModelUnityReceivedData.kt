package com.arupakaman.pluginpermissionhelper.models

import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
@Keep
data class ModelUnityReceivedData @JvmOverloads constructor(
    val unityReceivedDataJsonStr: String,
    val unityObjName: String = "",
    val unityStatusMethodName: String? = null,                      // will receive only one status json with true/false
    val unityInfoMethodName: String? = null,                        // will receive complete json response for each permission status
    val handleMandatory: Boolean = true,                            // handle mandatory permissions dialog in plugin, set false to handle in unity code
    val handleRational: Boolean = true,                             // handle show rational permissions dialog in plugin, set false to handle in unity code
) : Parcelable



fun JSONObject.jsonToModelUnityReceivedData(): ModelUnityReceivedData? {
    runCatching {
        return ModelUnityReceivedData(
            getString("permission_json_data"),
            getString("obj_name"),
            getString("unity_status_method_name"),
            getString("unity_info_method_name"),
            optBoolean("handle_mandatory", true),
            optBoolean("handle_rational", true),
        )
    }.onFailure {
        Log.e("UnityReceivedData", "pluginpermissionhelper : wrong unity data received!")
    }
    return null
}