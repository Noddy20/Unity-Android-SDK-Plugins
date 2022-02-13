# Unity Android SDK Plugins

Unity Android SDK Plugins is an Open Source project that contains code to generate Android SDK/Plugins (.aar files) that can be used in an Unity project to implement/access native Android features.

## Available Plugins :

- pluginbasicutils - Provides basic Android util functions i.e. to show a toast, to check network connectivity [know more](/pluginbasicutils/README_PLUGINBASICUTILS.md)

## Usage

- Download the plugin .aar file that you want to use in your Unity project from releases or build it by yourself and paste the .aar file in your Unity project's `Assets/Plugins/Android` directory.
- Use [External Dependency Manager](https://github.com/googlesamples/unity-jar-resolver) for Unity to resolve the dependencies used in the plugins.
- Add project config ext variables in project level gradle like:
```
    ext{
        compileTargetSDK = 31
        kotlin = '1.5.30'
    }
```
Check [build.gradle](build.gradle) for current config.
- Check out usage code snippet for each plugin in their respective READ_ME files.

### Request
If you need an Android unity aar plugin create an issue with feature request or mail us at arupakamanstudios[AT]gmail[DOT]com.

### I prefer a star than a cup of coffee

## Licensing

This project is licensed under the [MIT License.](LICENSE)