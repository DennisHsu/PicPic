package com.pingfun.picpic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by D.H on 2016/12/16.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private String[] mThumbFiles = new String[]{};

    private int imageSize = 100;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {

        return mThumbFiles.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int pixel = GenUtil.dpToPixel(mContext, imageSize);
            imageView.setLayoutParams(new GridView.LayoutParams(pixel, pixel));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if(mThumbFiles.length > 0){
            ImageAssetLoader.getInstance().loadImage(mThumbFiles[position], imageView);
        }

        return imageView;
    }

    public void seFiles(String[] mThumbFiles){

        if(mThumbFiles != null){
            this.mThumbFiles = mThumbFiles;
        }
    }

    public void setImageSize(int imageSize){

        this.imageSize = imageSize;
    }
}

