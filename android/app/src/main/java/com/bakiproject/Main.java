package com.bakiproject; // replace com.your-app-name with your app’s name

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;

public class Main extends ReactContextBaseJavaModule {
    Main(ReactApplicationContext context) {
        super(context);
    }

    // add to CalendarModule.java
    @Override
    public String getName() {
        return "Main";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public int createCalendarEvent() {
        return 31;
    }
}
