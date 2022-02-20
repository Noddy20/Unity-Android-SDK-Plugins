# Unity Android SDK Plugin Permission Helper

This unity android plugin provides android permission helper to check permission (granted or not) and allow you to ask android permission/permissions for your Unity project.

## Plugin Functions/Features :

- Check if a specific permission is already granted to your app or not.
- Allow you to request single/multiple permissions from user.
- Callbacks can be used to receive permission granted or not with status only or with full information.

## Plugin Usage :

In your unity project, you can use this plugins in your C# scripts.
Download the plugin .aar file (pluginpermissionhelper-debug.aar or pluginpermissionhelper-release.aar based on development environment) from releases or
build it by yourself and paste the .aar file in your Unity project's `Assets/Plugins/Android` directory. (Follow main README.md for plugin setup info)

### Common code required to access all functions

```
    const string pluginName = "com.arupakaman.pluginpermissionhelper.ui.RequestPermissionActivity";             // Constant of RequestPermissionActivity class location

    AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
    AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");            // jo will be used for Context param
    AndroidJavaClass ajcPermissionHelper = new AndroidJavaClass(pluginName);                       // ajcWebView will be used to access WebViewActivity class
```
### To Check if Permission is Granted or not

Below function returns true if permission is already granted.

```
    bool isGranted = ajcPermissionHelper.CallStatic<bool>(
                        "hasPermission",                                // function name to check permission
                        jo,                                             // first param context defined above
                        "android.permission.ACCESS_FINE_LOCATION",      // Permission name constant String [see more](https://developer.android.com/reference/android/Manifest.permission#constants)
                        );

```

### To Request Permission/Permissions

```
    // Note* : below variable jsonPermissionData is not in correct String format, make sure you create your json in String
    String jsonPermissionData = {
                                    "obj_name": "Name of the Object the Unity Script is attached to",
                                    "unity_status_method_name": "methodToReceivePermissionStatus with a String param",
                                    "unity_info_method_name": "methodToReceivePermissionDetailedInfo with a String param",    // you can also use one of the callback method unity_status_method_name or unity_info_method_name
                                    "handle_mandatory": "true/false to handle mandatory permission dialog in plugin (Optional, by default true)",
                                    "handle_rational": "true/false to handle mandatory permission dialog in plugin (Optional, by default true)",
                                    "permission_json_data": [
                                         {
                                            "perm_name": "android.permission.ACCESS_FINE_LOCATION",             //Use Constant values [from here](https://developer.android.com/reference/android/Manifest.permission#constants)
                                            "perm_visible_name": "Location Permission",                         // Name that will be visible to user in mandatory/rational dialogs
                                            "is_mandatory": "true/false    // true if permission is mandatory (Optional, by default true)"
                                         }
                                    ]
                                 }

    ajcPermissionHelper.CallStatic(
                "startRequestPermissionActivity",       // function name to launch Permission request Activity
                jo,                                     // first param context defined above
                jsonPermissionData                      // json as String with permission and callback information
                );


    // A valid jsonPermissionData variable example:
    String jsonPermissionData = "{\"obj_name\": \"Text\", \"unity_status_method_name\": \"PermissionGrantedStatus\", \"unity_info_method_name\": \"PermissionGrantedInfo\", \"handle_mandatory\": \"true\", \"handle_rational\": \"true\",\"permission_json_data\": [{\"perm_name\": \"android.permission.ACCESS_FINE_LOCATION\", \"perm_visible_name\": \"Location Permission\", \"is_mandatory\": \"true\"}]}"

```

### To receive permission request status callback

You can use one of the below methods or both of them :

1) You can implement a method with a String parameter to receive the overall permission status. This method will receive status in the String parameter with one of the below values :
    GRANTED  (If user has granted all the mandatory permissions)
    NOT_GRANTED  (If user has not granted any one of the mandatory permissions)
    SHOW_RATIONAL  (If user has selected not to ask again for a mandatory permission)

Status response example = {"status":"GRANTED"}
Send this method name in json key = "unity_status_method_name"

2) You can implement a method with a String parameter to receive the complete permission status information. This method will receive a json object with status of all requested permissions and other data as follows:

Example response = [{"perm_name":"android.permission.ACCESS_FINE_LOCATION","perm_visible_name":"Location Permission","is_mandatory":true,"is_granted":true}]
Send this method name in json key = "unity_info_method_name"

You can parse the response and use the keys to check which permission is granted and which is not.

### Customize dialog Strings

Override the below string resource in your Unity project to customize the error message :

```
     <!-- Override these strings in your Unity project to customize the dialog text -->

     <string name="dialog_message_rational">You need to grant required permission from device settings :</string>
     <string name="dialog_action_rational_positive">Grant Now</string>
     <string name="dialog_action_rational_negative">Not Now</string>
     <string name="dialog_message_perm_mandatory">You need to grant required permission :</string>
     <string name="dialog_action_perm_mandatory">Okay</string>
```

### I prefer a :star: than a cup of :coffee: