package com.pingfun.picpic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.pingfun.picpic.GenUtil;
import com.pingfun.picpic.R;
import com.pingfun.picpic.datamodel.PicObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "DrawView";

    private String SAVEFILE_PATH = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory()
            .getPath()))).append("/DCIM/").append(getResources().getString(R.string.ID_PIC_FLODER)).toString();

    private Context context;

    private SurfaceHolder holder;

    private ArrayList<PicObject> piclist;

    private Canvas outputCanvas;

    private Bitmap saveBitmap;

    private Bitmap bgBmp;

    private Bitmap scaledBitmap;

    private Paint paint;

    private ShowThread st;

    private int touchX = 0, touchY = 0;

    private int tempwidth = 0;

    private int tempheight = 0;

    private int viewHeight;

    private int viewWidth;

    private boolean tapFlag = false;

    // first tough distance
    private float oldLineDistance;

    private float oldRotation;

    private boolean isScaleFirst = true;

    private boolean isRotateFirst = true;

    Matrix matrix = new Matrix();

    public DrawView(Context context) {

        super(context);
        this.context = context;
        getHolder().addCallback(this);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        getHolder().addCallback(this);
    }

    // constructor
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        this.context = context;
        getHolder().addCallback(this);
        piclist = new ArrayList<PicObject>();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int rotation = display.getRotation();

        int screenWidth;
        int screenHeight;

		/* set height and width for draw view */
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            screenWidth = metrics.widthPixels;
            screenHeight = (int) (metrics.heightPixels * 0.9);
            viewHeight = screenHeight - GenUtil.dpToPixel(context, 20);
            viewWidth = screenWidth - GenUtil.dpToPixel(context, 20);
        } else {
            screenWidth = metrics.widthPixels;
            screenHeight = (int) (metrics.heightPixels * 0.9);
            viewWidth = screenWidth - GenUtil.dpToPixel(context, 20);
            viewHeight = screenHeight - GenUtil.dpToPixel(context, 20);
        }

		/* Set background */
        bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.color001);

        scaledBitmap = Bitmap.createScaledBitmap(bgBmp, viewWidth, viewHeight, true);
        saveBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        outputCanvas = new Canvas(saveBitmap);
        st = new ShowThread();

    }

    public void doDraw(Canvas canvas) {
        // canvas.drawColor(Color.CYAN);
        synchronized (outputCanvas) {
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            outputCanvas.drawBitmap(scaledBitmap, 0, 0, null);
            paint = new Paint();
            synchronized (piclist) {
                for (PicObject po : piclist) {
                    // canvas.drawColor(Color.BLACK);
                    canvas.save();
                    outputCanvas.save();

                    int w = po.getWidth() / 2;
                    int h = po.getHeight() / 2;

                    // 處理旋轉角度
                    canvas.rotate(po.getRotation(), po.getX() + w, po.getY() + h);
                    outputCanvas.rotate(po.getRotation(), po.getX() + w, po.getY() + h);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(6);
                    PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                    paint.setPathEffect(effects);
                    if (po.isCheckFlag()) {
                        // 處理選取外框
                        paint.setColor(context.getResources().getColor(R.color.app_frame_color));
//						 paint.setColor(Color.parseColor("#000000"));
                        canvas.drawRect(po.getX(), po.getY(), po.getX() + po.getWidth(), po.getY() + po.getHeight(),
                                paint);
                    }

                    // 縮放處理
                    canvas.scale(po.getScaleRate(), po.getScaleRate(), po.getX(), po.getY());
                    outputCanvas.scale(po.getScaleRate(), po.getScaleRate(), po.getX(), po.getY());
                    // 輸出至Bitmap
                    canvas.drawBitmap(po.getContent(), po.getX(), po.getY(), null);
                    outputCanvas.drawBitmap(po.getContent(), po.getX(), po.getY(), null);

                    canvas.restore();

                    outputCanvas.restore();

                }
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {

        PicObject selectPo = null;
        List<PicObject> selectList = new ArrayList<PicObject>();
        synchronized (piclist) {
            Log.d(TAG, "piclist.size() :" + piclist.size());
            for (int i = 0; i < piclist.size(); i++) {

                if (isInSide(piclist.get(i), (int) event.getX(), (int) event.getY())) {
                    selectList.add(piclist.get(i));
                }

                int j = 0;
                if (piclist.get(i).isCheckFlag()) {
                    j++;
                    selectPo = piclist.get(i);
                    Log.d(TAG, "selectPo :" + j);
                }
                Log.d(TAG, "PO-" + i + " :" + piclist.get(i).getScaleRate());

            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PicObject selectOne = null;
                int index = -1;
                double distance = 0;
                double tmp = 9999;

                if (selectList.size() != 0) {

                    for (int i = 0; i < selectList.size(); i++) {

                        PicObject po = selectList.get(i);
                        distance = Math.sqrt(Math.abs(event.getX() - po.getX()) + Math.abs(event.getY() - po.getY()));
                        if (distance < tmp) {
                            tmp = distance;
                            selectOne = po;
                            index = i;
                        }
                    }

                    if (selectOne != null) {
                        Log.d(TAG, "touch down");
                        touchX = (int) event.getX();
                        touchY = (int) event.getY();
                        Log.d(TAG, "touchX :" + touchX);
                        Log.d(TAG, "touchY :" + touchY);
                        tempwidth = touchX - selectOne.getX();
                        tempheight = touchY - selectOne.getY();
                        // Collections.swap(piclist, piclist.size() - 1, index);
                        Log.d(TAG, "piclist.indexOf(selectOne)1:" + piclist.indexOf(selectOne));

                        piclist.remove(selectOne);
                        piclist.add(piclist.size(), selectOne);

                        Log.d(TAG, "piclist.indexOf(selectOne)2:" + piclist.indexOf(selectOne));
                        for (PicObject po : piclist) {
                            po.setChkFlag(false);
                        }
                        selectOne.setChkFlag(true);
                    }

                } else {

                    if (selectList.size() == 0) {
                        for (PicObject po : piclist) {
                            po.setChkFlag(false);
                        }
                    }
                }
                tapFlag = true;

            } else if ((event.getAction() == MotionEvent.ACTION_MOVE) && event.getPointerCount() == 1 && selectPo != null
                    && clickRotateButton(selectPo, (int) event.getX(), (int) event.getY())) {// 旋轉鈕
                Log.d(TAG, "touch rotate button !");


            } else if ((event.getAction() == MotionEvent.ACTION_MOVE) && event.getPointerCount() == 1
                    && selectPo != null && isInSide(selectPo, (int) event.getX(), (int) event.getY())) {
                // 手指在畫面移動
                Log.d(TAG, "touch move");
                if (!tapFlag) {
                    return true;
                }
                touchX = (int) event.getX();
                touchY = (int) event.getY();


                selectPo.setX(touchX - tempwidth);
                selectPo.setY(touchY - tempheight);
                selectPo.getContainer().offsetTo(selectPo.getX(), selectPo.getY());

            } else if ((event.getAction() == MotionEvent.ACTION_MOVE) && event.getPointerCount() > 1
                    && selectPo != null && selectPo.isCheckFlag()) {
                // && isInSide(selectPo, (int)event.getX(),(int)event.getY())) {
                Log.d(TAG, "touch scale");
                tapFlag = false;
                if (isScaleFirst) {
                    oldLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2)
                            + Math.pow(event.getY(1) - event.getY(0), 2));
                    isScaleFirst = false;
                } else {
                    //
                    float newLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2)
                            + Math.pow(event.getY(1) - event.getY(0), 2));
                    // scale
                    float rate;
                    rate = selectPo.getOldScaleRate() * newLineDistance / oldLineDistance;
                    // rate = selectPo.getScaleRate() * newLineDistance /
                    // oldLineDistance;
                    Log.d(TAG, "scale rate:" + rate);
                    if (rate > 3) {
                        rate = 3;
                    }
                    selectPo.setScaleRate(rate);
                    Log.d(TAG,
                            "selectPo.getWidth():" + selectPo.getWidth() + ",selectPo.getHeight():"
                                    + selectPo.getHeight());
                    selectPo.setWidth((int) (selectPo.getContent().getWidth() * rate));
                    selectPo.setHeight((int) (selectPo.getContent().getHeight() * rate));
                    Log.d(TAG,
                            "selectPo.getWidth():" + selectPo.getWidth() + ",selectPo.getHeight():"
                                    + selectPo.getHeight());
                }
                if (isRotateFirst) {
                    oldRotation = rotation(event);
                    isRotateFirst = false;
                } else {
                    //
                    float rotation = rotation(event) - oldRotation;
                    // if(Math.abs(rotation)>2){
                    selectPo.setRotation(selectPo.getOldRotation() + (int) rotation);
                    // }

                }
            } else if ((event.getAction() == MotionEvent.ACTION_MOVE) && event.getPointerCount() > 1
                    && selectPo != null && selectPo.isCheckFlag()) {

                Log.d(TAG, "touch Rotate");

            } else if (event.getAction() == MotionEvent.ACTION_UP && selectPo != null) {// 手指離開畫面

                isScaleFirst = true;
                isRotateFirst = true;
                selectPo.setOldScaleRate(selectPo.getScaleRate());
                selectPo.setOldRotation(selectPo.getRotation());
            }

        }
        return true;
    }

    public void addPic(Bitmap content) {
        synchronized (piclist) {
            piclist.add(PicObject.getInstance(content));
            // piclist.add(0, PicObject.getInstance(content));
        }
    }

    public void setBackground(Bitmap content) {
        synchronized (outputCanvas) {
            scaledBitmap = Bitmap.createScaledBitmap(content, viewWidth, viewHeight, true);
        }
    }

    public File saveBitmap() {
        synchronized (outputCanvas) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File f = new File(SAVEFILE_PATH + System.currentTimeMillis() + ".jpg");

            try {
                f.createNewFile();

                // write the bytes in file
                FileOutputStream fo;

                fo = new FileOutputStream(f);

                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return f;
        }
    }

    public void deletePicObject(){

        synchronized (piclist) {
            for (PicObject po : piclist) {
                if (po.isCheckFlag()) {
                    piclist.remove(po);
                }
            }
        }

    }
    public class ShowThread extends Thread {

        Canvas canvas;
        boolean flag = false;
        int span = 120;

        // constructor
        public ShowThread() {
            flag = true;
        }

        public void run() {
            while (flag) {
                try {
                    synchronized (holder) {

                        canvas = holder.lockCanvas();
                        if (canvas != null) {
                            // canvas.setBitmap(saveBitmap);
                            doDraw(canvas);
                        }
                        holder.unlockCanvasAndPost(canvas);
                    }
                    Thread.sleep(span);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "thread state:" + st.getState());
        if (st.getState() == Thread.State.NEW) {
            st.start();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        st.interrupt();
    }

    private boolean isInSide(PicObject po, int ex, int ey) {

        int lx = po.getX();
        int ly = po.getY();
        int rx = po.getX() + po.getWidth();
        int ry = po.getY() + po.getHeight();

        if ((ex >= lx && ex <= rx) && (ey >= ly && ey <= ry)) {
            return true;
        }
        return false;
    }

    private boolean clickRotateButton(PicObject po, int ex, int ey) {

        int lx = po.getX() + po.getWidth() - 25;
        int ly = po.getY() + po.getHeight() - 25;
        int rx = po.getX() + po.getWidth() + 25;
        int ry = po.getY() + po.getHeight() + 25;

        if ((ex >= lx && ex <= rx) && (ey >= ly && ey <= ry)) {
            return true;
        }
        return false;
    }

    // 取旋轉角度
    private float rotationSingle(MotionEvent event) {
        double delta_x = (event.getX() - 0);
        double delta_y = (event.getY() - 0);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 取旋轉角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public Bitmap getBgBmp() {
        return bgBmp;
    }

    public void setBgBmp(Bitmap bgBmp) {
        this.bgBmp = bgBmp;
    }

}
