package com.example;

import android.content.Intent;

import com.facebook.react.ReactActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maxiaoyao.imagepicker.ImagePicker;
import com.maxiaoyao.imagepicker.data.ImageBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends ReactActivity {
    public static ArrayBlockingQueue<String> mQueue = new ArrayBlockingQueue<String>(1);
    private final int PICTURE_SELECT_REQUEST_CODE = 111;
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "Example";
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICTURE_SELECT_REQUEST_CODE) {
            return;
        }
        if (resultCode == RESULT_OK && data != null) {
            List<ImageBean> resultList = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            String content = "";
            Gson gson = new Gson();
            Type type = new TypeToken<List<ImageBean>>() {
            }.getType();
            content = gson.toJson(resultList, type);
            mQueue.add(content);
        } else{
            mQueue.add("no_pciture_data");
        }
    }

}
