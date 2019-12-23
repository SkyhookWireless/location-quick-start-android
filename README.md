# Skyhook Location SDK: Quick Start for Android

The Quick Start project illustrates minimal steps to integrate the SDK into your app.

## Add SDK to your project

Add Skyhook's Maven repository URL to the `repositories` section in your `build.gradle`:
```gradle
repositories {
    maven { url 'https://skyhookwireless.github.io/skyhook-location-android' }
}
```

Add SDK to the `dependencies` section:
```gradle
dependencies {
    implementation 'com.skyhook.location:location-sdk-android:5.2+'
}
```

## Import the SDK

Import the Skyhook WPS package:
```java
import com.skyhookwireless.wps;
```

## Initialize API

Create a new instance of the `XPS` class in the `onCreate` method of your activity or application class and set your API key:
```java
private IWPS xps;
...
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    xps = new XPS(this);
    xps.setKey("YOUR KEY");
    ...
}
```

Make sure to replace `"YOUR KEY"` with your real API key in the Quick Start app source code (see the `onCreate()` method in `MainActivity.java`).

You can obtain the API key from [my.skyhook.com](https://my.skyhook.com), creating an account and a new Precision Location project.

## Request location permission

In order to be able to start location determination, your app must first obtain the `ACCESS_FINE_LOCATION` permission from the user:
```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    ActivityCompat.requestPermissions(
        this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
}
```

## Request location

Once the location permission has been granted, you can make a locaton request.

Note that the callback methods are invoked from the background thread, so you have to use `runOnUiThread()` to manipulate the UI.
```java
xps.getLocation(null, WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP, false, new WPSLocationCallback() {
    @Override
    public void handleWPSLocation(WPSLocation location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // display location
            }
        });
    }

    @Override
    public WPSContinuation handleError(WPSReturnCode error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // display error
            }
        });

        return WPSContinuation.WPS_CONTINUE;
    }

    @Override
    public void done() {
        // after done() returns, you can make more XPS calls
    }
});
```

**Note**: [starting with Android P](https://developer.android.com/guide/topics/connectivity/wifi-scan#wifi-scan-throttling) a fresh Wi-Fi location can only be calculated four times during a 2-minute period.
