package com.nativemodulesdemo;

import com.facebook.react.bridge.Promise;

public interface Crop {
    void selectWithCrop(int aspectX,int aspectY, Promise promise);
}
