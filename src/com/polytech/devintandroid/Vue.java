package com.polytech.devintandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class Vue extends SurfaceView implements
		android.view.SurfaceHolder.Callback {

	LinearLayout			mLinearLayout;

	private SurfaceHolder	holder;
	Canvas					canvas;
	GameLoop				game;

	public Vue(Context context, int car, int level) {
		super(context);
		this.holder = getHolder();
		this.holder.addCallback(this);
		setFocusable(true);
		canvas = new Canvas();
		this.game = new GameLoop(context, holder, car, level);
		mLinearLayout = new LinearLayout(context);
	}

	/** Rafraichir l'écran */
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
	 * et renseigner les dimensions de l'écran
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("mg", "Surface changed, width = [" + width + "], height = ["
				+ height + "]");

		game.setSwidth(width);
		game.setSheight(height);
		this.invalidate(holder);
		// this.buffer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		// this.canvas = new Canvas(buffer);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		game.setSwidth(this.getMeasuredWidth());
		game.setSheight(this.getMeasuredHeight());
		game.setRunning(true);
		game.start();
	}

	/**
	 * Arrêt du jeu et join des threads
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		game.setRunning(false);
		boolean alive = true;
		while (alive) {
			try {
				game.join();
				alive = false;
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
	}
	public void speedBoostOnTouch(){
		if(!this.game.isInBoost()){
			this.game.setSpeed(this.game.getSpeed()+1000);
			this.game.setInBoost(true);
		}
		
	}
	public void speedBoostOnRelease(){
		if(this.game.isInBoost()){
			this.game.setSpeed(this.game.getSpeed()-1000);
			this.game.setInBoost(false);
		}
	}

}
