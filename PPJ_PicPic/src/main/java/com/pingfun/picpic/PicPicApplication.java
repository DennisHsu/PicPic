package com.pingfun.picpic;

import android.app.Application;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by D.H on 2017/1/4.
 */

public class PicPicApplication extends Application {

    public final static String FOLDER_NAME = "PicPic";

    public final static String SAVE_FILE_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator
            + Environment.DIRECTORY_DCIM + File.separator + FOLDER_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        /* init ImageLoader*/
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        /* inti ImageAssetLoader*/
        ImageAssetLoader.getInstance().init(this);

        File dir = new File(SAVE_FILE_DIR);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
    }
}
