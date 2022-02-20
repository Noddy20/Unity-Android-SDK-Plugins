# Unity Android SDK Plugin WebView

This unity android plugin provides android WebView implementation.

## Plugin Functions/Features :

- Launch WebView with a from your app Url
- Override the <activity android:name=".WebViewActivity"> in your Unity Manifest to implement deep link handling for your app.
- Customize WebView UI (Toolbar visibility, color, etc.)

## Plugin Usage :

In your unity project, you can use this plugins in your C# scripts.
Download the plugin .aar file (pluginwebview-debug.aar or pluginwebview-release.aar based on development environment) from releases or
build it by yourself and paste the .aar file in your Unity project's `Assets/Plugins/Android` directory. (Follow main README.md for plugin setup info)

### Common code required to access all functions

```
    const string pluginName = "com.arupakaman.pluginwebview.WebViewActivity";             // Constant of WebViewActivity class location

    AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
    AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");            // jo will be used for Context param
    AndroidJavaClass ajcWebView = new AndroidJavaClass(pluginName);                       // ajcWebView will be used to access WebViewActivity class
```

### To launch WebView

```
    ajcWebView.CallStatic(
                "startWebViewActivity",                 // function name to launch WebView Activity
                jo,                                     // first param context defined above
                "My GitHub Profile",                    // WebView Title (can keep empty if )
                "https://github.com/Noddy20",           // Web url to load (can also load .pdf)
                true,                                   // true to show Toolbar with title, back button and refresh button (Optional param, by default it's true)
                1,                                      // To set WebView orientation portrait only (1), for landscape only (0) (Optional param, by default it's -1 which will keep orientation auto)
                true,                                   // true to enable refresh option (Optional param, by default it's true)
                true);                                  // true to enable JavaScript on WebView (Optional param, by default it's true)

```

### To configure deep links

To configure deep links for your app override the WebView activity in your Unity project Manifest like below example :

```
     <activity
        android:name=".WebViewActivity"
        android:exported="true"
        android:launchMode="singleTask"
        android:theme="@style/Theme.WebViewTheme"             <!-- You can also change the theme here ->
        >
        <!-- Customize the host and pathPrefix in your Unity project Manifest file (override the <activity>) for deep link handling, like below example -->
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />

            <data android:scheme="https"/>
            <data android:scheme="http"/>
            <data android:host="github.com"/>
            <data android:pathPrefix="/Noddy20"/>

        </intent-filter>
     </activity>
```

### Customize error message

Override the below string resource in your Unity project to customize the error message :

```
    <string name="err_msg_general_error_web_view">Something went wrong!\nSwipe down to refresh.</string>
```

### Customize Theme or colors

```
     <style name="Theme.WebViewTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
                <!-- Use theme Theme.WebViewTheme to Override the WebView Activity theme in your Unity project -->

                <!-- If you want to use different theme or just want to override the colors then define these attributes in that theme with your colors in your Unity project -->
                <item name="colorWebViewToolbarBackground">#00C43E</item>
                <item name="colorWebViewToolbarText">#ffffff</item>
                <item name="colorWebViewToolbarIcon">#ffffff</item>
     </style>
```

### I prefer a :star: than a cup of :coffee: