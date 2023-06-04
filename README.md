# Youtube-Shorts-Clone

This is sample application in native android using Kotkin inspired from YouTube Shorts. 
* RecyclerView and ViewPager2 used to display lists of video.  
* Retrofit was used to get data from the backend URL.
* ViewModel was created to share the data between the fragments.
* Exoplayer 3 was used to play video in a continous fashion.
* SimpleCache is used to cache previously played videos to enable seemless playback

## Table of Contents
1. [Setup. Install IDE](#setup)
1. [Build. Create apk](#building)
1. [ProGuard](#proguard)
2. [Screenshots](#screenshots)
3. [Credits](#credits)

## Setup
### Starting from base project
1. `git clone https://github.com/josephalx/Youtube-Shorts-Clone.git`
1. `cd Youtube-Shorts-Clone`

### IDE
1. Download latest Android Studio from https://developer.android.com/studio/index.html
1. Follow Android Studio installation instruction.
1. Download and install latest JDK 11 https://www.openlogic.com/openjdk-downloads.
1. Open Android Studio - Open Existing Android Project - find folder with project and click `OK`
1. Wait a while. Follow Android Studio instructions to install missing items.
1. Press `cmd + shift + a` and type `AVD Manager` and press Enter.
1. Press `Create Virtual Device...` button.
1. Select `Pixel 6`
1. Select latest API level (in case if latest is not available then click `Download` and wait, it's going to take a while).
1. Click `Next`
1. Click `Finish`

## Building
### Create APK
After you complete the `Gradle` project configuration, you can use `gradlew` executable to build the APK:
```bash
$ ./gradlew assembleDebug       // to build a debug APK
$ ./gradlew assembleRelease     // to build a release signed APK, can upload to Market
```
Or use Android Studio build tool build >> Build Bundle(s)/APK(s) >> Build APK(s)
### Install
To install app on emulator or connected real device:
```bash
$ ./gradlew installDebug
```
## ProGuard
Project already has proguard config for included libraries.
Maintain [proguard-rules.pro](https://github.com/josephalx/Youtube-Shorts-Clone/blob/main/app/proguard-rules.pro) updated when you add new libraries or play with reflection.
When you add new library or check out its Proguard section and add rules to `proguard-rules.pro`.
When you add code which uses reflection add rules to `proguard-rules.pro`.

## Screenshots
<div align="center">
     <img src="/Screenshots/Screenshot_20230603_193054.png" width="200px"/> 
    <img src="/Screenshots/Screenshot_20230603_192749.png" width="200px" /> 
    <img src="/Screenshots/Screenshot_20230603_192844.png" width="200px"/> 
    <img src="/Screenshots/Screenshot_20230603_192903.png" width="200px"/> 
</div>


## Credits
This YouTube Shorts Clone app is maintianed by [Joseph Alex Chakola](https://github.com/josephalx). The open API from [Gro.care](https://gro.care/) was used to obtain videos for this app.
