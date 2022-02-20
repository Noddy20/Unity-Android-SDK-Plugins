package com.arupakaman.pluginpermissionhelper.models

import androidx.annotation.Keep
import android.os.Parcelable
import android.util.Log
import com.arupakaman.pluginpermissionhelper.utils.JsonObject
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
@Keep
data class ModelPermission @JvmOverloads constructor(
    val permName: String,                                               // Permission name by Android Framework
    val permInfoName: String,                                           // Permission name to show to the user in dialogs
    val isMandatory: Boolean = true,                                    // true if permission is mandatory
    var isGranted: Boolean = false
) : Parcelable


fun ModelPermission.toJson(): JsonObject{
    return JsonObject {
        "perm_name" to permName
        "perm_visible_name" to permInfoName
        "is_mandatory" to isMandatory
        "is_granted" to isGranted
    }
}

fun Array<ModelPermission>.toJson(): JSONArray{
    val jsonArray = JSONArray()
    this.forEach {
        jsonArray.put(it.toJson())
    }
    return jsonArray
}

fun JSONObject.jsonToModelPermission(): ModelPermission? {
    runCatching {
        return ModelPermission(
            getString("perm_name"),
            optString("perm_visible_name", getString("perm_name")),
            optBoolean("is_mandatory", true),
            optBoolean("is_granted", false)
        )
    }.onFailure {
        Log.e("ModelPermission", "pluginpermissionhelper : wrong permission json!")
    }
    return null
}

fun JSONArray.jsonToModelPermissionsList(): List<ModelPermission>? {
    runCatching {
        val list = ArrayList<ModelPermission>()
        repeat(this.length()){
            getJSONObject(it).jsonToModelPermission()?.let { model-> list.add(model)}
        }
        return list
    }.onFailure {
        Log.e("ModelPermission", "pluginpermissionhelper : wrong permission json!")
    }
    return null
}