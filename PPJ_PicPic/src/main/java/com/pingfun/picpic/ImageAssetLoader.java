package com.pingfun.picpic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by D.H on 2017/1/3.
 */

public class ImageAssetLoader {

    private final static String TAG = "ImageAssetLoader";

    private static class SingletonHolder {
        private static ImageAssetLoader instance = new ImageAssetLoader();
    }

    private AssetManager assets = null;

    private ImageLoader imageLoader;

    public static ImageAssetLoader getInstance() {
        return SingletonHolder.instance;
    }

    public void init(Context context) {

        assets = context.getAssets();
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
    }

    public String[] getFileList(String folder) {

        Log.d(TAG, "assets:" + assets + ",name:" + folder);
        if (assets == null) {
            return null;
        }
        try {

            String[] fileList = assets.list(folder);

            for (int i = 0; i < fileList.length; i++) {
                Log.d(TAG, "fileName:" + fileList[i]);
                fileList[i] = folder + File.separator + fileList[i];
            }
            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadImage(String url, ImageView imageView) {

        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //  which implements ImageAware interface)
        String imageUri = "assets://" + url;
        imageLoader.displayImage(imageUri, imageView);
    }

    public Bitmap loadImage(String url) {

        String imageUri = "assets://" + url;
        // Load image, decode it to Bitmap and return Bitmap synchronously
        Bitmap bmp = imageLoader.loadImageSync(imageUri);

        return bmp;
    }

}
