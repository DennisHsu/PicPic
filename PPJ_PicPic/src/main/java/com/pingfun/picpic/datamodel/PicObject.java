package com.pingfun.picpic.datamodel;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class PicObject {

    private boolean chkFlag = false;

    private boolean scaleFlag = false;

    private Bitmap content;

    private Rect container;  //矩形容器

    private int x;

    private int y;

    private int width;

    private int height;

    private float scaleRate;

    private float oldScaleRate;

    private float rotation;

    private float oldRotation;

    private PicObject(Bitmap content) {
        this.content = content;
        this.x = 0;
        this.y = 0;
        this.width = content.getWidth();
        this.height = content.getHeight();
        this.container = new Rect(x, y, content.getWidth(), content.getHeight());
        this.scaleRate = 1.0f;
        this.oldScaleRate = 1.0f;
        this.oldRotation = 0;
        this.rotation = 0;
    }

    public static PicObject getInstance(Bitmap content) {
        return new PicObject(content);
    }

    public boolean isCheckFlag() {
        return chkFlag;
    }

    public void setChkFlag(boolean chkFlag) {
        this.chkFlag = chkFlag;
    }

    public Bitmap getContent() {
        return content;
    }

    public void setContent(Bitmap content) {
        this.content = content;
    }

    public Rect getContainer() {
        return container;
    }

    public void setContainer(Rect container) {
        this.container = container;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getScaleRate() {
        return scaleRate;
    }

    public void setScaleRate(float scaleRate) {
        this.scaleRate = scaleRate;
    }

    public float getOldScaleRate() {
        return oldScaleRate;
    }

    public void setOldScaleRate(float oldScaleRate) {
        this.oldScaleRate = oldScaleRate;
    }

    public boolean isScaleFlag() {
        return scaleFlag;
    }

    public void setScaleFlag(boolean scaleFlag) {
        this.scaleFlag = scaleFlag;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getOldRotation() {
        return oldRotation;
    }

    public void setOldRotation(float oldRotation) {
        this.oldRotation = oldRotation;
    }

}
