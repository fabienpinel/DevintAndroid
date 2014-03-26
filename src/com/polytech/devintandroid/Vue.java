package com.polytech.devintandroid;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class Vue extends SurfaceView implements android.view.SurfaceHolder.Callback {

	LinearLayout			mLinearLayout;
	

	private Bitmap			buffer;			// pixel buffer
	private SurfaceHolder	holder;
	Canvas					canvas;
	GameLoop				game;
	private int car;

	public Vue(Context context, int car) {
		super(context);
		this.holder = getHolder();
		this.holder.addCallback(this);
		setFocusable(true);
		canvas = new Canvas();
		this.game = new GameLoop(context, holder, car);
		// Create a LinearLayout in which to add the ImageView
		mLinearLayout = new LinearLayout(context);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("RunGameActivity", "OnTouchEvent");
		synchronized (this) {
		}
		return true;
	}
	/** Rafraichir l'écran*/
	public void invalidate(SurfaceHolder holder) {
	  if (holder != null) {
	   Canvas c = holder.lockCanvas();
	   if (c != null) {
		   canvas.drawColor(0, Mode.CLEAR);
	    holder.unlockCanvasAndPost(c);
	   }
	  }
	 }

	/**
	 * callback lorsque la surface est chargée, donc démarrer la boucle de jeu
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		 Log.d("mg", "Surface changed, width = [" + width + "], height = ["
	                + height + "]");
		game.setSwidth(width);
		game.setSheight(height);
		this.invalidate(holder);
		//this.buffer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		//this.canvas = new Canvas(buffer);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		game.setSwidth(this.getMeasuredWidth());
		game.setSheight(this.getMeasuredHeight());
		game.setRunning(true);
		game.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("mg", "Surface destroyed");
        game.setRunning(false);
        boolean alive = true;
        while (alive) {
            try {
                game.join();
                alive = false;
            } catch (InterruptedException e) {
            }
        }
	}

	
}
