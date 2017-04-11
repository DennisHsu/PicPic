package com.pingfun.picpic.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pingfun.picpic.Constant;
import com.pingfun.picpic.GenUtil;
import com.pingfun.picpic.ImageAssetLoader;
import com.pingfun.picpic.PicPicApplication;
import com.pingfun.picpic.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    public final static String TAG = "MainActivity";

    public final static int REQ_OPEN_CAMERA = 3;

    public final static int REQ_GET_BG = 2;

    public final static int REQ_GET_ICON = 1;

    public final static int REQ_GET_PIC = 0;

    private DrawView drawView;

    private ImageButton textBtn;

    private ImageButton openFileBtn;

    private ImageButton addIconBtn;

    private ImageButton changeBgBtn;

    private ImageButton camearaBtn;

    private ToggleButton menuBtn;

    private LinearLayout menuLinearLayout;

    private AlertDialog dialog;

    private Paint paint;

    private Canvas canvas;

    private Bitmap textBitmap;

    private ProgressDialog progressDialog;

    private LayoutInflater textEditorInflater;

    private View textEditorLayout;

    private EditText editField;

    private String outputFileUri;

    /* Text Editor*/
    private ImageView limegreenBtn;

    private ImageView mediumblueBtn;

    private ImageView forestgreenBtn;

    private ImageView whiteBtn;

    private ImageView blackBtn;

    private ImageView darkvioletBtn;

    private ImageView deeppinkBtn;

    private ImageView royalblueBtn;

    private ImageView yellowBtn;

    private ImageView grayBtn;

    private ImageView redBtn;

    private ImageView orangeredBtn;

    private int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_02));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        findViews();

        initData();

        setListener();

    }

    private void findViews() {

        drawView = (DrawView) findViewById(R.id.TakeView);

        Log.d(TAG, "W:" + drawView.getWidth());
        Log.d(TAG, "H:" + drawView.getHeight());

        progressDialog = new ProgressDialog(this);

        menuLinearLayout = (LinearLayout) findViewById(R.id.MENU_LL);

        changeBgBtn = (ImageButton) findViewById(R.id.CHANGE_BG_BTN);

        textBtn = (ImageButton) findViewById(R.id.TEXT_BTN);

        openFileBtn = (ImageButton) findViewById(R.id.INSERT_BTN);

        menuBtn = (ToggleButton) findViewById(R.id.Menu_ICON);

        addIconBtn = (ImageButton) findViewById(R.id.INSERT_ICON_BTN);

        camearaBtn = (ImageButton) findViewById(R.id.CAMERA_BTN);
    }

    private void initData() {

    }

    private void setListener() {

        textBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                menuBtn.performClick();

                createTextEditorDialog();

            }
        });

        openFileBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                menuBtn.performClick();
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                Intent destIntent = Intent.createChooser(intent, "");

                startActivityForResult(destIntent, REQ_GET_PIC);

            }

        });
        addIconBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                menuBtn.performClick();
                Intent intent = new Intent(MainActivity.this,
                        IconListDialog.class);

                startActivityForResult(intent, REQ_GET_ICON);

            }

        });

        changeBgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                menuBtn.performClick();
                Intent intent = new Intent(MainActivity.this,
                        BackgroundListDialog.class);

                startActivityForResult(intent, REQ_GET_BG);

            }

        });


        menuBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (menuBtn.isChecked()) {
                    menuLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    menuLinearLayout.setVisibility(View.INVISIBLE);
                }

            }

        });

        camearaBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                menuBtn.performClick();

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQ_OPEN_CAMERA);

                File tmpFile = new File(PicPicApplication.SAVE_FILE_DIR, "PicPic_" + "tmp" + ".jpg");
                outputFileUri = tmpFile.getAbsolutePath();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
                startActivityForResult(intent, REQ_OPEN_CAMERA);


            }
        });
    }

    private void createTextEditorDialog() {

        textEditorInflater = LayoutInflater
                .from(MainActivity.this);

        textEditorLayout = textEditorInflater.inflate(R.layout.dialog_text_editor, null);

        editField = (EditText) textEditorLayout.findViewById(R.id.Edit_Field);

        limegreenBtn = (ImageView) textEditorLayout.findViewById(R.id.limegreen_btn);

        mediumblueBtn = (ImageView) textEditorLayout.findViewById(R.id.mediumblue_btn);

        forestgreenBtn = (ImageView) textEditorLayout.findViewById(R.id.forestgreen_btn);

        whiteBtn = (ImageView) textEditorLayout.findViewById(R.id.white_btn);

        blackBtn = (ImageView) textEditorLayout.findViewById(R.id.black_btn);

        darkvioletBtn = (ImageView) textEditorLayout.findViewById(R.id.darkviolet_btn);

        deeppinkBtn = (ImageView) textEditorLayout.findViewById(R.id.deeppink_btn);

        royalblueBtn = (ImageView) textEditorLayout.findViewById(R.id.royalblue_btn);

        yellowBtn = (ImageView) textEditorLayout.findViewById(R.id.yellow_btn);

        grayBtn = (ImageView) textEditorLayout.findViewById(R.id.gray_btn);

        redBtn = (ImageView) textEditorLayout.findViewById(R.id.red_btn);

        orangeredBtn = (ImageView) textEditorLayout.findViewById(R.id.orangered_btn);

        limegreenBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFF32CD32;
                editField.setTextColor(color);
            }
        });
        mediumblueBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFF0000CD;
                editField.setTextColor(color);
            }
        });
        forestgreenBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFF228B22;
                editField.setTextColor(color);
            }
        });
        whiteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFFFFFFFF;
                editField.setTextColor(color);
            }
        });
        blackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFF000000;
                editField.setTextColor(color);

            }
        });
        darkvioletBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                color = 0xFF9400D3;
                editField.setTextColor(color);
            }
        });
        deeppinkBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                color = 0xFFFF1493;
                editField.setTextColor(color);
            }
        });
        royalblueBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFF4169E1;
                editField.setTextColor(color);

            }
        });
        yellowBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFFFFFF00;
                editField.setTextColor(color);

            }
        });
        grayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                color = 0xFF808080;
                editField.setTextColor(color);

            }
        });
        redBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                color = 0xFFFF0000;
                editField.setTextColor(color);
            }
        });
        orangeredBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                color = 0xFFFF4500;
                editField.setTextColor(color);

            }
        });

        dialog = new AlertDialog.Builder(
                MainActivity.this)
                .setPositiveButton(
                        getResources().getString(R.string.ID_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {

                                dialog.dismiss();

                                String testString = editField.getText().toString();

                                paint = new Paint();
                                paint.setStrokeWidth(5);
                                paint.setTextSize(150);
                                paint.setShadowLayer(3, 1, 1, Color.WHITE);
//									    String testString = "Hello 123";
                                Rect rect = new Rect();
                                paint.getTextBounds(testString, 0, testString.length(), rect);
                                Log.d("dh", "rect.width():" + rect.width());
                                Log.d("dh", "rect.height():" + rect.height());
                                textBitmap = Bitmap.createBitmap(rect.width() + 50, rect.height() + 100, Bitmap.Config.ARGB_8888);
                                canvas = new Canvas(textBitmap);
                                paint.setColor(color);
                                canvas.drawText(testString, 25, rect.height() + 50, paint);
                                drawView.addPic(textBitmap);
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(textEditorLayout, 0, 0, 0, 0);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.ID_DELETE_MSG)
                        .setPositiveButton(
                                getResources().getString(R.string.ID_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        drawView.deletePicObject();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.ID_CANCEL),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                    }
                                }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();


                break;
            case R.id.action_save:

                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.ID_SAVE_MSG)
                        .setPositiveButton(
                                getResources().getString(R.string.ID_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        savePicture();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.ID_CANCEL),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                    }
                                }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case R.id.action_share:

                sharePicture();

                break;
            default:
                break;
        }

        return true;
    }

    private void savePicture() {
        progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.ID_WAITING), false);
        new Thread() {
            public void run() {

                try {
                    File file = drawView.saveBitmap();
                    updateGallery(file.getAbsolutePath());
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }

        }.start();
    }


    private void sharePicture() {
        progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.ID_WAITING), false);
        new Thread() {
            public void run() {

                try {
                    File file = drawView.saveBitmap();
                    updateGallery(file.getAbsolutePath());
                    Thread.sleep(1000);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse("file://" + file.getAbsolutePath()));
                    startActivity(Intent.createChooser(intent, getString(R.string.app_name)
                            .toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }

        }.start();
    }

    @Override
    protected void onResume() {
        drawView = (DrawView) findViewById(R.id.TakeView);
//		drawView.setDrawingCacheEnabled(true);
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Resources res = getResources();

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQ_GET_PIC:

                    setPicToCanvas(data);
                    break;

                case REQ_GET_ICON:

                    String icon = data.getExtras().getString(Constant.ICON);

                    Bitmap iconBmp = ImageAssetLoader.getInstance().loadImage(icon);
                    iconBmp = ThumbnailUtils.extractThumbnail(iconBmp, GenUtil.dpToPixel(this, 120), GenUtil.dpToPixel(this, 120));
                    drawView.addPic(iconBmp);
                    break;

                case REQ_GET_BG:

                    String bg = data.getExtras().getString(Constant.BACKGROUND);

                    Bitmap bgBmp = ImageAssetLoader.getInstance().loadImage(bg);
                    drawView.setBackground(bgBmp);
                    break;

                case REQ_OPEN_CAMERA:
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(outputFileUri, options);
                    options.inSampleSize = GenUtil.calculateInSampleSize(options, 512, 512);
                    options.inJustDecodeBounds = false;
                    Bitmap tmpBmp = BitmapFactory.decodeFile(outputFileUri, options); // 利用BitmapFactory去取得剛剛拍照的圖像
                    drawView.addPic(tmpBmp);

                    break;

            }


        } else {//
            Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setPicToCanvas(Intent data) {

        Uri uri = data.getData();
        if (uri != null) {
            Bitmap bmp = null;
            try {

                InputStream in = getContentResolver().openInputStream(uri);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, opts);
                in.close();

                int sampleSize = computeSampleSize(opts, -1, 512 * 512);


                in = getContentResolver().openInputStream(uri);
                opts = new BitmapFactory.Options();
                opts.inSampleSize = sampleSize;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                bmp = BitmapFactory.decodeStream(in, null, opts);
                in.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            if (bmp != null) {
                drawView.addPic(bmp);
            }

        } else {
            Toast.makeText(MainActivity.this, "wrong path",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGallery(String file) {
        File f = new File(file);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;

        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }


    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
