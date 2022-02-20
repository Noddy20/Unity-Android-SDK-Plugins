package com.arupakaman.pluginpermissionhelper.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.arupakaman.pluginpermissionhelper.R
import com.arupakaman.pluginpermissionhelper.models.*
import com.arupakaman.pluginpermissionhelper.unity.UnityBridge
import com.arupakaman.pluginpermissionhelper.utils.*
import org.json.JSONArray
import org.json.JSONObject


class RequestPermissionActivity : Activity(){

    companion object {

        const val EXTRA_KEY_PERMISSIONS_MODELS = "PermissionModels"
        const val EXTRA_KEY_UNITY_RECEIVED_DATA_MODEL = "ModelUnityReceivedData"
        const val PERMISSION_REQUEST_CODE = 2901

        /**
         *   Function to request permission with json data
         *   unityReceivedData =
         *   {
         *     "obj_name": "Name of the Object the Unity Script is attached to",
         *     "unity_status_method_name": "methodToReceivePermissionStatus",
         *     "unity_info_method_name": "methodToReceivePermissionDetailedInfo",
         *     "handle_mandatory": "true/false to handle mandatory permission dialog in plugin",
         *     "handle_rational": "true/false to handle mandatory permission dialog in plugin",
         *     "permission_json_data": [
         *           {
         *              "perm_name": "android.permission.ACCESS_FINE_LOCATION",             //Use Constant values from here - https://developer.android.com/reference/android/Manifest.permission#constants
         *              "perm_visible_name": "Location Permission",
         *              "is_mandatory": "true/false    // true if permission is mandatory",
         *              "is_granted": "true/false  //Optional all requested permissions will be granted false So ignore it",
         *           }
         *     ]
         *   }
         */
        @JvmStatic
        fun startRequestPermissionActivity(mContext: Context, unityReceivedData: String) {
            unityReceivedData.let { jsonStr->
                kotlin.runCatching {
                    JSONObject(jsonStr).jsonToModelUnityReceivedData()?.let {unityData->
                        JSONArray(unityData.unityReceivedDataJsonStr).jsonToModelPermissionsList()?.let { models->
                            if (!models.isNullOrEmpty()){
                                startRequestPermissionActivity(mContext, models, unityData)
                            }
                        }
                    }
                }.onFailure {
                    Log.e("RequestPermissionAct", "startRequestPermissionsActivity Data Error -> $it")
                }
            }
        }

        @JvmStatic
        fun startRequestPermissionActivity(mContext: Context, permissions: List<ModelPermission>, unityReceivedData: ModelUnityReceivedData) {
            with(mContext) {
                startActivity(
                    Intent(this, RequestPermissionActivity::class.java).apply {
                        putParcelableArrayListExtra(EXTRA_KEY_PERMISSIONS_MODELS,
                            ArrayList(permissions))
                        putExtra(EXTRA_KEY_UNITY_RECEIVED_DATA_MODEL, unityReceivedData)
                    }
                )
            }
        }

        @JvmStatic
        fun hasPermission(context: Context, permission: String) = context.hasPermissions(arrayOf(permission))

    }

    private var alertDialog: AlertDialog? = null

    private lateinit var modelUnityReceivedData: ModelUnityReceivedData
    private lateinit var arrPermissionModels: Array<ModelPermission>
    private lateinit var arrPermissionMandatory: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getParcelableArrayListExtra<ModelPermission>(EXTRA_KEY_PERMISSIONS_MODELS).let {pList->
            if (pList.isNullOrEmpty()){
                finish()
            }else{
                val unityData = intent.getParcelableExtra<ModelUnityReceivedData?>(EXTRA_KEY_UNITY_RECEIVED_DATA_MODEL)
                if (unityData == null){
                    finish()
                    return
                }
                modelUnityReceivedData = unityData
                arrPermissionModels = pList.toTypedArray()
                arrPermissionMandatory = pList.filter { it.isMandatory }.map { it.permName }.toTypedArray()
                reqPermissions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermissions(arrPermissionMandatory)) {
            sendStatusResponseToUnity(EnumPermStatus.GRANTED.name)
            finish()
        }else{
            reqPermissions()
        }
    }

    override fun onDestroy() {
        dismissDialog()
        super.onDestroy()
    }

    private fun reqPermissions(){
        val permsToReq = arrPermissionModels.map { it.permName }.toTypedArray()
        if (hasPermissions(permsToReq)){
            sendStatusResponseToUnity(EnumPermStatus.GRANTED.name)
            finish()
        }else{
            requestPermissionsExt(permsToReq, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            arrPermissionModels.forEach { itB->
                itB.isGranted = hasPermissions(arrayOf(itB.permName))
            }

            if (hasPermissions(arrPermissionMandatory)){
                sendStatusResponseToUnity(EnumPermStatus.GRANTED.name)
                finish()
            }else{
                if (shouldShowRational(*arrPermissionMandatory)){
                    sendStatusResponseToUnity(EnumPermStatus.NOT_GRANTED.name)
                    if (modelUnityReceivedData.handleRational) {
                        showRationalPermissionDialog()
                    }else finish()
                }else {
                    sendStatusResponseToUnity(EnumPermStatus.NOT_GRANTED.name)
                    if (modelUnityReceivedData.handleMandatory) {
                        showMandatoryPermissionDialog()
                    }else finish()
                }
            }
        }
    }

    private fun showMandatoryPermissionDialog(){
        dismissDialog()
        AlertDialog.Builder(this).let { builder ->
            builder.setMessage(getString(R.string.dialog_message_perm_mandatory) + "\n\n" +
                    arrPermissionModels.filter { it.isMandatory }.joinToString(",\n") { it.permInfoName.replace("android.permission.", "") })
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_action_perm_mandatory)) { dialog, _ ->
                    reqPermissions()
                    dialog.dismiss()
                }
            alertDialog = builder.create()
            alertDialog?.show()
        }
    }

    private fun showRationalPermissionDialog(){
        dismissDialog()
        AlertDialog.Builder(this).let { builder ->
            builder.setMessage(getString(R.string.dialog_message_rational) + "\n\n" + arrPermissionModels.filter { it.isMandatory }
                .joinToString(", ") { it.permInfoName })
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_action_rational_positive)) { dialog, _ ->
                    goToAppSettings()
                    dialog.dismiss()
                }.setNegativeButton(getString(R.string.dialog_action_rational_negative)){dialog, _ ->
                    sendStatusResponseToUnity(EnumPermStatus.NOT_GRANTED.name)
                    dialog.dismiss()
                    finish()
                }
            alertDialog = builder.create()
            alertDialog?.show()
        }
    }

    private fun dismissDialog(){
        alertDialog?.let {dialog->
            if (dialog.isShowing) dialog.dismiss()
            alertDialog = null
        }
    }

    private fun sendStatusResponseToUnity(status: String){
        modelUnityReceivedData.run {
            unityStatusMethodName?.let { statusMethod->
                UnityBridge.sendMessage(unityObjName, statusMethod, "{\"status\":\"${status}}\"")         // status = GRANTED, NOT_GRANTED, SHOW_RATIONAL
            }
            unityInfoMethodName?.let { infoMethod->
                UnityBridge.sendMessage(unityObjName, infoMethod, arrPermissionModels.toJson().toString())
            }
        }
    }

}