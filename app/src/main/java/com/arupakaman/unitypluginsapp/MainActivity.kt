package com.arupakaman.unitypluginsapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.arupakaman.pluginbasicutils.unity.UnityUtils
import com.arupakaman.pluginpermissionhelper.ui.RequestPermissionActivity
import com.arupakaman.pluginwebview.WebViewActivity
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvHello = findViewById<TextView>(R.id.tvHello)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            var result = "Net ${UnityUtils.isNetConnected(this)}\n"
            result += "Wifi Conn ${UnityUtils.isWifiConnected(this)}\n"
            result += "Wifi En ${UnityUtils.isWifiEnabled(this)}\n"
            result += "Hotspot ${UnityUtils.isHotspotEnabled(this)}\n"
            result += "BlueTooth ${UnityUtils.isBluetoothEnabled(this)}\n"
            result += "AndroidId ${UnityUtils.getAndroidDeviceId(this)}\n"
            result += "DevMode ${UnityUtils.isDeveloperModeEnabled(this)}\n"
            result += "isRooted ${UnityUtils.isRooted(this)}\n"
            result += "isRootedOrHarmApps ${UnityUtils.isRootedOrHarmfulAppsInstalled(this)}\n"
            result += "isGpsEnabled ${UnityUtils.isGpsEnabled(this)}\n"
            result += "Battery ${UnityUtils.getBatteryPercentage(this)}\n"
            tvHello.text = result
        }

        //WebViewActivity.startWebViewActivity(this, "My GitHub Profile", "https://github.com/Noddy20", isToolbarEnabled = false)

        val json = JSONObject().apply {
            put("obj_name", "")
            put("unity_status_method_name", "abc")
            put("unity_info_method_name", "abc")
            put("handle_mandatory", true)
            put("handle_rational", true)
            put("permission_json_data", JSONArray().apply {
                put(JSONObject().apply {
                    put("perm_name", Manifest.permission.ACCESS_FINE_LOCATION)
                    put("perm_full_name", "Fine Location")
                })
                put(JSONObject().apply {
                    put("perm_name", Manifest.permission.ACCESS_COARSE_LOCATION)
                    put("perm_full_name", "Coarse Location")
                })
            })
        }
        Log.d("MainActivity", "startRequestPermission -> $json")
        RequestPermissionActivity.startRequestPermissionActivity(this, json.toString())

    }
}