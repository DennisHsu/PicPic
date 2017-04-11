package com.pingfun.picpic.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pingfun.picpic.Constant;
import com.pingfun.picpic.ImageAdapter;
import com.pingfun.picpic.ImageAssetLoader;
import com.pingfun.picpic.R;

public class IconListDialog extends Activity {

    private ImageAdapter imageAdapter;

    private String[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_icon_list);

        GridView gridview = (GridView) findViewById(R.id.icon_gridview);

        files = ImageAssetLoader.getInstance().getFileList(Constant.ICON);

        imageAdapter = new ImageAdapter(this);
        imageAdapter.seFiles(files);
        imageAdapter.setImageSize(100);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putString(Constant.ICON, files[position]);
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
            }
        });


    }

}
