package com.maxiaoyao.imagepicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

import com.maxiaoyao.imagepicker.data.ImageContants;
import com.maxiaoyao.imagepicker.data.ImageDataModel;
import com.maxiaoyao.imagepicker.data.ImagePickType;
import com.maxiaoyao.imagepicker.data.ImagePickerCropParams;
import com.maxiaoyao.imagepicker.data.ImagePickerOptions;
import com.maxiaoyao.imagepicker.ui.grid.view.ImageDataActivity;
import com.maxiaoyao.imagepicker.utils.IImagePickerDisplayer;

/**
 *
 * TODO 调用方法的入口
 */

public class ImagePicker
{
    /**
     * 返回结果中包含图片数据的Intent的键值
     */
    public static final String INTENT_RESULT_DATA = "ImageBeans";

    private ImagePickerOptions mOptions;

    public ImagePicker()
    {
        mOptions = new ImagePickerOptions();
    }

    public ImagePicker pickType(ImagePickType mode)
    {
        mOptions.setType(mode);
        return this;
    }

    public ImagePicker maxNum(int maxNum)
    {
        mOptions.setMaxNum(maxNum);
        return this;
    }

    public ImagePicker needCamera(boolean b)
    {
        mOptions.setNeedCamera(b);
        return this;
    }

    public ImagePicker cachePath(String path)
    {
        mOptions.setCachePath(path);
        return this;
    }

    public ImagePicker doCrop(ImagePickerCropParams cropParams)
    {
        mOptions.setNeedCrop(cropParams != null);
        mOptions.setCropParams(cropParams);
        return this;
    }

    public ImagePicker doCrop(int aspectX, int aspectY, int outputX, int outputY)
    {
        mOptions.setNeedCrop(true);
        mOptions.setCropParams(new ImagePickerCropParams(aspectX, aspectY, outputX, outputY));
        return this;
    }

    public ImagePicker displayer(IImagePickerDisplayer displayer)
    {
        ImageDataModel.getInstance().setDisplayer(displayer);
        return this;
    }

    /**
     * 发起选择图片
     *
     * @param activity    发起的Activity
     * @param requestCode 请求码
     */
    public void start(Activity activity, int requestCode)
    {
        if (activity==null) {
            Log.e("imagePicker", "Activity context is null");
        }
        Intent intent = new Intent(activity, ImageDataActivity.class);
        intent.putExtra(ImageContants.INTENT_KEY_OPTIONS, mOptions);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 发起选择图片
     *
     * @param fragment    发起的Fragment
     * @param requestCode 请求码
     */
    public void start(Fragment fragment, int requestCode)
    {
        Intent intent = new Intent(fragment.getActivity(), ImageDataActivity.class);
        intent.putExtra(ImageContants.INTENT_KEY_OPTIONS, mOptions);
        fragment.startActivityForResult(intent, requestCode);
    }
}
