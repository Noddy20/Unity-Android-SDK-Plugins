package com.arupakaman.unitypluginsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.arupakaman.pluginbasicutils.unity.UnityUtils
import com.arupakaman.pluginwebview.WebViewActivity

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

        WebViewActivity.startWebViewActivity(this, "My GitHub Profile", "https://github.com/Noddy20", isToolbarEnabled = false)

    }
}