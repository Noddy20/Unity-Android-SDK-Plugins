# Unity Android SDK Plugins Basic Utils

This plugin provides basic Android util functions.

## Plugin Functions/Features :

- Show Android Toast UI/Popup (Short/Long)
- Check if device has Internet connection
- Check if device is connected to a WiFi
- Check if WiFi is enabled on device
- Check if Hotspot is enabled on device
- Check if Bluetooth is enabled on device
- Check if Developer Mode (USB debugging) is enabled
- Check if Device is Rooted
- Check if Device is Rooted or has any harmful app installed
- Check if Location Service (GPS) is enabled  
- Get Android Device Id (Settings.Secure.ANDROID_ID or SSAID)
- Get Current Device Battery Percentage

## Plugin Usage :

In your unity project, you can use this plugins in your C# scripts.
Download the plugin .aar file (pluginbasicutils-debug.aar or pluginbasicutils-release.aar based on development environment) from releases or
build it by yourself and paste the .aar file in your Unity project's `Assets/Plugins/Android` directory. (Follow main README.md for plugin setup info)

### Common code required to access all functions

```
    const string pluginName = "com.arupakaman.pluginbasicutils.unity.UnityUtils";          // Constant of util static class location

    AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
    AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");            // jo will be used for Context param
    AndroidJavaClass ajc = new AndroidJavaClass(pluginName);                              // ajc will be used to access UnityUtils class
```

### To show Android Toast UI/Popup (Short/Long)

```
    ajc.CallStatic("toast", jo, "Hello Unity World!");            // toast is function name to show the Toast
    ajc.CallStatic("toastLong", jo, "Hello Unity World!");        // for long toast
```

### To check boolean returning functions

```
    bool isNetConnected = ajc.CallStatic<bool>("isNetConnected", jo);
    bool isWifiConnected = ajc.CallStatic<bool>("isWifiConnected", jo);
    bool isWifiEnabled = ajc.CallStatic<bool>("isWifiEnabled", jo);
    bool isHotspotEnabled = ajc.CallStatic<bool>("isHotspotEnabled", jo);
    bool isBluetoothEnabled = ajc.CallStatic<bool>("isBluetoothEnabled", jo);
    bool isDeveloperModeEnabled = ajc.CallStatic<bool>("isDeveloperModeEnabled", jo);
    bool isRooted = ajc.CallStatic<bool>("isRooted", jo);
    bool isRootedOrHarmfulAppsInstalled = ajc.CallStatic<bool>("isRootedOrHarmfulAppsInstalled", jo);
    bool isGpsEnabled = ajc.CallStatic<bool>("isGpsEnabled", jo);
```

### To get Android Device ID

```
    string androidDeviceId = ajc.CallStatic<string>("getAndroidDeviceId", jo);
```

### Current Device Battery Percentage

```
    int batteryPercentage = ajc.CallStatic<int>("getBatteryPercentage", jo);
```

### I prefer a star than a cup of coffee