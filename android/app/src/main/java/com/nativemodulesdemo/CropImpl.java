package com.nativemodulesdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;

import java.util.ArrayList;

public class CropImpl implements ActivityEventListener, Crop {

    private static final String TAG = "CropImpl";

    private final int RC_PICK = 50081;
    private final int RC_CROP = 50082;
    private final String CODE_ERROR_PICK = "用户取消";
    private final String CODE_ERROR_CROP = "裁剪失败";

    private Promise pickPromise;
    private Uri outputUri;
    private int aspectX;
    private int aspectY;
    private Activity activity;

    public static CropImpl of(Activity activity) {
        return new CropImpl(activity);
    }

    private CropImpl(Activity activity) {
        this.activity = activity;
    }

    public void updateActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void selectWithCrop(int outputX, int outputY, Promise promise) {
        Log.d(TAG, "selectWithCrop: outputX:" + outputX + " outputY:" + outputY + " activity:" + activity);
        this.aspectX = outputX;
        this.aspectY = outputY;
        this.pickPromise = promise;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImagePicker.withMulti(new WeChatPresenter())
                        .mimeTypes(MimeType.ofImage())
                        .filterMimeTypes(MimeType.GIF)
                        //剪裁完成的图片是否保存在DCIM目录下
                        //true：存储在DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
                        .cropSaveInDCIM(false)
                        //设置剪裁比例
                        .setCropRatio(outputX, outputY)
                        //设置剪裁框间距，单位px
                        .cropRectMinMargin(50)
                        //是否圆形剪裁，圆形剪裁时，setCropRatio无效
//                        .cropAsCircle()
                        //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
                        .cropStyle(CropConfig.STYLE_FILL)
                        //设置留白模式下生成的图片背景色，支持透明背景
                        .cropGapBackgroundColor(Color.TRANSPARENT)
                        .crop(CropImpl.this.activity, new OnImagePickCompleteListener2() {
                            @Override
                            public void onPickFailed(PickerError error) {
                                Log.d(TAG, "onPickFailed: " + error);
                                if (pickPromise != null) {
                                    pickPromise.reject(CODE_ERROR_CROP, "裁剪失败");
                                }
                            }

                            @Override
                            public void onImagePickComplete(ArrayList<ImageItem> items) {
                                //图片剪裁回调，主线程
                                Log.d(TAG, "onImagePickComplete: " + items);
                                if (items != null && items.size() > 0) {
                                    pickPromise.resolve(items.get(0).getCropUrl());
                                } else {
                                    pickPromise.reject(CODE_ERROR_CROP, "裁剪失败");
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        // startActivityForResult 的情况会回调这个方法
        /*if (requestCode == RC_PICK) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                outputUri = Uri.fromFile(Utils.getPhotoCacheDir(System.currentTimeMillis()));
                onCrop(data.getData(), outputUri);
            } else {
                pickPromise.reject(CODE_ERROR_PICK, "没有获取到结果");
            }
        } else if (requestCode == RC_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                pickPromise.resolve(outputUri.getPath());
            } else {
                pickPromise.reject(CODE_ERROR_CROP, "裁剪失败");
            }
        }*/
    }

//    private void onCrop(Uri targetUri, Uri outputUri) {
//        this.activity.startActivityForResult(IntentUtils.getCropIntentWith(targetUri, outputUri), RC_CROP);
//    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
