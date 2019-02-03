# BitGym Cardio w/ Unity (Android)

## This project contains:
- Android Studio Project
  * exported Unity android project
  * modifications done to the Unity android project to support BitGym Cardio integration
  * .aar files for the BitGym Cardio SDK
- Unity Project (coming soon)

## Android Studio Project
The Android studio project is a modified output of Unity's `Export as Android Studio Project`  build option. Rerunning the build option from Unity will overwrite the classes here to include changes to the scene, gameObjects, and scripts.

### Building the APK
> NOTE: Substitute $\*$ with the appropriate value

You can load the project up into [Android Studio](https://developer.android.com/studio/) and buils it using the Build > Build APK(s) menu action within AS.

To build from the terminal (Mac), navigate to the AS project folder root (this folder)
```
$ chmod 755 gradlew
$ ./gradlew assembleDebug --debug
```
The `chmod` only needs to be run the first time per project folder. To build to release, use ```$ ./gradlew assembleRelease```. However you will need to add app alias, keystore signing key etc to `build.gradle` (which is sync'd to git) via a file named `keystore.properties` (which shouldn't be sync'd to git). See [this page of the android developer docs](https://developer.android.com/studio/publish/app-signing#secure_key) for more details.

To test, you will need adb.

In general, run
```
$ adb install -r $module-name$/build/outputs/apk/$module-name$-debug.apk
$ adb logcat -c
$ adb shell am start -n $package-name$/$package-name$.$MainActivityName$
```
For a clean reinstall, first run
```
$ adb uninstall com.activetheoryinc.unitycardiosample
```
before installing the above

## Unity Project
(more details coming soon)
