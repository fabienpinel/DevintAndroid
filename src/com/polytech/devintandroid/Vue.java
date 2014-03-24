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

	public Vue(Context context) {
		super(context);
		this.holder = getHolder();
		this.holder.addCallback(this);
		setFocusable(true);
		canvas = new Canvas();
		this.game = new GameLoop(context, holder);
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

	protected void onDraw(Canvas canvas) {
		// Draw the background image. Operations on the Canvas accumulate
		// so this is like clearing the screen.
		// canvas.drawBitmap(mBackgroundImage, 0, 0, null);

		//super.onDraw(canvas);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		// Draw the shadow
		// canvas.drawRect(0, 150, 150, 250, p);
		/*
		 * canvas.drawLine(0, 0, 100, 200, p); canvas.drawLine(100, 200, 50,
		 * 400, p); canvas.drawLine(0, 0, 50, 400, p);
		 * 
		 * canvas.drawLine(50, 400, 100, 700, p); canvas.drawLine(100, 700, 20,
		 * 1000, p);
		 * 
		 * canvas.drawLine(20, 1000, 300, 1280, p);
		 * 
		 * canvas.drawLine(100, 0, 100, 1280, p);
		 */

		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setStrokeWidth(1);
		p.setColor(Color.WHITE);
		Path path = new Path();
		// COTE GAUCHE
		/*
		 * this.affichageDesPoints(path, p, canvas);
		 * this.avancer(tousLesPoints); this.invalidate();
		 */

		/*
		 * path.moveTo(0, 0); path.lineTo(200, 0); path.lineTo(0, 300);
		 * path.close(); path.offset(0, 0); canvas.drawPath(path, p);
		 * 
		 * path.moveTo(0, 300); path.lineTo(200, 500); path.lineTo(0, 1000);
		 * path.close(); path.offset(0, 0); canvas.drawPath(path, p);
		 * 
		 * Path triangle2 = new Path(); triangle2.moveTo(0, 1000);
		 * triangle2.lineTo(200, 1200); triangle2.lineTo(0, 1280);
		 * triangle2.close(); triangle2.offset(0, 0); canvas.drawPath(triangle2,
		 * p);
		 * 
		 * 
		 * 
		 * 
		 * Path path2 = new Path(); path2.moveTo(800, 0); path2.lineTo(600, 0);
		 * path2.lineTo(800, 500); path2.close(); path2.offset(0, 0);
		 * canvas.drawPath(path2, p);
		 * 
		 * Path path3 = new Path(); path3.moveTo(800, 500); path3.lineTo(600,
		 * 900); path3.lineTo(800, 1280); path3.close(); path3.offset(0, 0);
		 * canvas.drawPath(path3, p);
		 */

		// 160*340
		Bitmap car = BitmapFactory.decodeResource(getResources(),
				R.drawable.car);
		canvas.drawBitmap(car, (game.getSwidth() / 2) - 80, game.getSheight() - 300, null);

	}
	/** Rafraichir l'écran*/
	@Override
	 public void invalidate() {
	  if (holder != null) {
	   Canvas c = holder.lockCanvas();
	   if (c != null) {
		  // canvas.drawColor(0, Mode.CLEAR);
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
		this.invalidate();
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
