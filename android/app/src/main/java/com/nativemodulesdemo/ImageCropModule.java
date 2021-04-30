package com.nativemodulesdemo;

import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ImageCropModule extends ReactContextBaseJavaModule implements Crop {

    private ReactApplicationContext reactContext;
    private CropImpl cropImpl;

    public ImageCropModule(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "ImageCrop";
    }

    @ReactMethod
    @Override
    public void selectWithCrop(int aspectX, int aspectY, Promise promise) {
        getCrop().selectWithCrop(aspectX, aspectY, promise);
    }

    @ReactMethod
    public void getScreenSize() {
        WritableMap params = Arguments.createMap();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.reactContext.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        params.putInt("screenWidth", width);
        params.putInt("screenHeight", height);
        sendEvent(this.reactContext, "eventScreenSize", params);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private CropImpl getCrop() {
        if (cropImpl == null) {
            cropImpl = CropImpl.of(this.reactContext.getCurrentActivity());
            this.reactContext.addActivityEventListener(cropImpl);
        } else {
            cropImpl.updateActivity(this.reactContext.getCurrentActivity());
        }
        return cropImpl;
    }
}
