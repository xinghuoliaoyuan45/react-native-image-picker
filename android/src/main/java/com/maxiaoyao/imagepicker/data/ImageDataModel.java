package com.maxiaoyao.imagepicker.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.maxiaoyao.imagepicker.R;
import com.maxiaoyao.imagepicker.utils.GlideImagePickerDisplayer;
import com.maxiaoyao.imagepicker.utils.IImagePickerDisplayer;
import com.maxiaoyao.imagepicker.utils.ImageComparator;
import com.maxiaoyao.imagepicker.utils.ImagePickerComUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片数据层
 */
public class ImageDataModel
{
    private ImageDataModel()
    {
    }

    private static final class ImageDataModelHolder
    {
        private static final ImageDataModel instance = new ImageDataModel();
    }

    public static ImageDataModel getInstance()
    {
        return ImageDataModelHolder.instance;
    }

    //所有图片
    private List<ImageBean> mAllImgList = new ArrayList<>();

    //所有文件夹List
    private List<ImageFloderBean> mAllFloderList = new ArrayList<>();

    //选中的图片List
    private List<ImageBean> mResultList = new ArrayList<>();

    //图片显示器
    private IImagePickerDisplayer mDisplayer;

    /**
     * 获取图片加载器对象
     *
     * @return 如果未设置则默认为GlideImagePickerDisplayer
     */
    public IImagePickerDisplayer getDisplayer()
    {
        return mDisplayer != null ? mDisplayer : (mDisplayer = new GlideImagePickerDisplayer());
    }

    /**
     * 设置图片加载器对象
     *
     * @param displayer 需要实现IImagePickerDisplayer接口
     */
    public void setDisplayer(IImagePickerDisplayer displayer)
    {
        this.mDisplayer = displayer;
    }

    /**
     * 获取所有图片数据List
     */
    public List<ImageBean> getAllImgList()
    {
        return mAllImgList;
    }

    /**
     * 获取所有文件夹数据List
     */
    public List<ImageFloderBean> getAllFloderList()
    {
        return mAllFloderList;
    }

    /**
     * 获取所有已选中图片数据List
     */
    public List<ImageBean> getResultList()
    {
        return mResultList;
    }

    /**
     * 添加新选中图片到结果中
     */
    public boolean addDataToResult(ImageBean imageBean)
    {
        if (mResultList != null)
            return mResultList.add(imageBean);
        return false;
    }

    /**
     * 移除已选中的某图片
     */
    public boolean delDataFromResult(ImageBean imageBean)
    {
        if (mResultList != null)
            return mResultList.remove(imageBean);
        return false;
    }

    /**
     * 判断是否已选中某张图
     */
    public boolean hasDataInResult(ImageBean imageBean)
    {
        if (mResultList != null)
            return mResultList.contains(imageBean);
        return false;
    }

    /**
     * 获取已选中的图片数量
     */
    public int getResultNum()
    {
        return mResultList != null ? mResultList.size() : 0;
    }

    /**
     * 扫描图片数据
     *
     * @param c context
     * @return 成功或失败
     */
    public boolean scanAllData(Context c)
    {
        try
        {
            Context context = c.getApplicationContext();
            //清空容器
            if (mAllImgList == null)
                mAllImgList = new ArrayList<>();
            if (mAllFloderList == null)
                mAllFloderList = new ArrayList<>();
            if (mResultList == null)
                mResultList = new ArrayList<>();
            mAllImgList.clear();
            mAllFloderList.clear();
            mResultList.clear();
            //创建“全部图片”的文件夹
            ImageFloderBean allImgFloder = new ImageFloderBean(
                    ImageContants.ID_ALL_IMAGE_FLODER, context.getResources().getString(R.string.imagepicker_all_image_floder));
            mAllFloderList.add(allImgFloder);
            //临时存储所有文件夹对象的Map
            ArrayMap<String, ImageFloderBean> floderMap = new ArrayMap<>();

            //索引字段
            String columns[] =
                    new String[]{MediaStore.Images.Media._ID,//照片id
                            MediaStore.Images.Media.BUCKET_ID,//所属文件夹id
                            //                        MediaStore.Images.Media.PICASA_ID,
                            MediaStore.Images.Media.DATA,//图片地址
                            MediaStore.Images.Media.WIDTH,//图片宽度
                            MediaStore.Images.Media.HEIGHT,//图片高度
                            //                        MediaStore.Images.Media.DISPLAY_NAME,//图片全名，带后缀
                            //                        MediaStore.Images.Media.TITLE,
                            //                        MediaStore.Images.Media.DATE_ADDED,//创建时间？
                            MediaStore.Images.Media.DATE_MODIFIED,//最后修改时间
                            //                        MediaStore.Images.Media.DATE_TAKEN,
                            //                        MediaStore.Images.Media.SIZE,//图片文件大小
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,//所属文件夹名字
                    };


            //得到一个游标
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            if (cur != null && cur.moveToFirst())
            {
                //图片总数
                allImgFloder.setNum(cur.getCount());

                // 获取指定列的索引
                int imageIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int imagePathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int imageModifyIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                int imageWidthIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int imageHeightIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int floderIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                int floderNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                do
                {
                    String imageId = cur.getString(imageIDIndex);
                    String imagePath = cur.getString(imagePathIndex);
                    String lastModify = cur.getString(imageModifyIndex);
                    String width = cur.getString(imageWidthIndex);
                    String height = cur.getString(imageHeightIndex);
                    String floderId = cur.getString(floderIdIndex);
                    String floderName = cur.getString(floderNameIndex);
                    //                    Log.e("ImagePicker", "imageId=" + imageId + "\n"
                    //                            + "imagePath=" + imagePath + "\n"
                    //                            + "lastModify=" + lastModify + "\n"
                    //                            + "width=" + width + "\n"
                    //                            + "height=" + height + "\n"
                    //                            + "floderId=" + floderId + "\n"
                    //                            + "floderName=" + floderName);

                    if (new File(imagePath).exists())
                    {
                        //创建图片对象
                        ImageBean imageBean = new ImageBean();
                        imageBean.setImageId(imageId);
                        imageBean.setImagePath(imagePath);
                        imageBean.setLastModified(ImagePickerComUtils.isNotEmpty(lastModify) ? Long.valueOf(lastModify) : 0);
                        imageBean.setWidth(ImagePickerComUtils.isNotEmpty(width) ? Integer.valueOf(width) : 0);
                        imageBean.setHeight(ImagePickerComUtils.isNotEmpty(height) ? Integer.valueOf(height) : 0);
                        imageBean.setFloderId(floderId);
                        mAllImgList.add(imageBean);
                        //更新文件夹对象
                        ImageFloderBean floderBean = null;
                        if (floderMap.containsKey(floderId))
                            floderBean = floderMap.get(floderId);
                        else
                            floderBean = new ImageFloderBean(floderId, floderName);
                        floderBean.setFirstImgPath(imagePath);
                        floderBean.gainNum();
                        floderMap.put(floderId, floderBean);
                    }

                } while (cur.moveToNext());
                cur.close();
            }

            //根据最后修改时间来降序排列所有图片
            Collections.sort(mAllImgList, new ImageComparator());
            //设置“全部图片”文件夹的第一张图片
            allImgFloder.setFirstImgPath(mAllImgList.size() != 0 ? mAllImgList.get(0).getImagePath() : null);
            //统一所有文件夹
            mAllFloderList.addAll(floderMap.values());

            return true;
        } catch (Exception e)
        {
            Log.e("ImagePicker", "ImagePicker scan data error:" + e);
            return false;
        }
    }

    /**
     * 根据文件夹获取该文件夹下所有图片数据
     *
     * @param floderBean 文件夹对象
     * @return 图片数据list
     */
    public List<ImageBean> getImagesByFloder(ImageFloderBean floderBean)
    {
        if (floderBean == null)
            return null;

        String floderId = floderBean.getFloderId();
        if (ImagePickerComUtils.isEquals(ImageContants.ID_ALL_IMAGE_FLODER, floderId))
        {
            return mAllImgList;
        } else
        {
            ArrayList<ImageBean> resultList = new ArrayList<>();
            int size = mAllImgList.size();
            for (int i = 0; i < size; i++)
            {
                ImageBean imageBean = mAllImgList.get(i);
                if (imageBean != null && ImagePickerComUtils.isEquals(floderId, imageBean.getFloderId()))
                    resultList.add(imageBean);
            }
            return resultList;
        }
    }

    /**
     * 释放资源
     */
    public void clear()
    {
        mDisplayer = null;
        mAllImgList.clear();
        mAllFloderList.clear();
        mResultList.clear();
    }
}
