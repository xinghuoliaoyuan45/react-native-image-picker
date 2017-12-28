package com.lwkandroid.imagepicker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.lwkandroid.imagepicker.data.ImagePickerCropParams;
import com.lwkandroid.imagepicker.utils.GlideImagePickerDisplayer;


import java.io.File;


public class PictureSelectNaviteModule extends ReactContextBaseJavaModule  {

     private String path = "";
     private ImagePickType PickType;
    private final int REQUEST_CODE = 111;

    public PictureSelectNaviteModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "AndroidPictureSelect";
    }//返回的这个名字是必须的，在rn代码中需要这个名字来调用该类的方法。

    @ReactMethod
    public void startPictureSelect(int maxNum,int imagePickType,Callback func, Callback errorBack){
        if(path == null || path.length() <= 0){

            path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/image/headportrait";
            //新建一个File，传入文件夹目录
            File file = new File(path);
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        if(maxNum==0){
           maxNum=1;
        }
        switch (imagePickType){
            case 1:
                PickType=ImagePickType.ONLY_CAMERA;
                break;
            case 2:
                PickType=ImagePickType.SINGLE;
                break;
            case 3:
                PickType=ImagePickType.MULTI;
                break;
            default:
                PickType=ImagePickType.ONLY_CAMERA;
                break;

        }
        ImagePickerCropParams ImagePickerCropParams=new ImagePickerCropParams(1, 1, 0, 0);
        try {

            new ImagePicker()
                    .pickType(PickType)// 1.ImagePickType.ONLY_CAMERA 2.ImagePickType.SINGLE 3.ImagePickType.MULTI设置选取类型(拍照、单选、多选)
                    .maxNum(maxNum)//这是需要传入的参数 设置最大选择数量(拍照和单选都是1，修改后也无效)
                    .needCamera(true)// true false 是否需要在界面中显示相机入口(类似微信) 默认显示
                    .cachePath(path)//自定义缓存路径
                    .doCrop(ImagePickerCropParams)//裁剪功能需要调用这个方法，多选模式下无效
                    .displayer(new GlideImagePickerDisplayer())//自定义图片加载器，默认是Glide实现的,可自定义图片加载器
                    .start(getCurrentActivity(), REQUEST_CODE);

            func.invoke(MainActivity.mQueue.take());

        } catch (Exception e) {
            func.invoke(e.getMessage());
            e.printStackTrace();
        }

    }


}
