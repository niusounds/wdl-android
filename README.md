# WDL for Android

JNI wrapper for [WDL](https://www.cockos.com/wdl/).

WDL is licensed under open source license. See official website.
https://www.cockos.com/wdl/

I'm not affiliated with Cockos but I followed the same license for my codes.

## How to use

```gradle
// root/build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

// app/build.gradle
dependencies {
    implementation 'com.github.niusounds:wdl-android:0.0.1'
}
```