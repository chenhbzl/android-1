/*
 * Copyright (C) 2010 Zhang YangJing
 * 
 * zhangyangjing@gmail.com
 * 
 */

package com.example.plasma;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;


public class AnimActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(new PlasmaView(this));
	}
}

class PlasmaView extends View implements View.OnTouchListener{	
	private Bitmap mBitmap;
	long time;
	long fps;

    public PlasmaView(Context context) {
        super(context);        
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(),R.drawable.mm);
        mBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);
        AnimRender.setBitmap(bmp);
        this.setOnTouchListener(this);
    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	long ct = System.currentTimeMillis();
    	if(ct - time > 1000){
    		Log.v("Fps:" + String.valueOf(fps));
    		time = ct;
    		fps = 0;
    	}
    	fps++;    	
    	
        AnimRender.render(mBitmap);    	
        canvas.drawBitmap(mBitmap, 0, 0, null);
        postInvalidate();
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		AnimRender.drop((int)event.getX(), (int)event.getY(), 500);
		return false;
	}
}

class AnimRender{
	public static native void setBitmap(Bitmap src);
    public static native void render(Bitmap dst);
    public static native void drop(int x, int y, int height);
   
    static {
        System.loadLibrary("plasma");
    }
}
