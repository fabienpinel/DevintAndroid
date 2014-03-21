package com.polytech.devintandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Vue extends View {

	LinearLayout	mLinearLayout;

	public Vue(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Create a LinearLayout in which to add the ImageView
		mLinearLayout = new LinearLayout(this.getContext());

		// Instantiate an ImageView and define its properties
		ImageView i = new ImageView(this.getContext());
		i.setImageResource(R.drawable.ic_launcher);
		i.setAdjustViewBounds(true);
		// set the ImageView bounds to match the
		// Drawable's dimensions
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		// Add the ImageView to the layout and set the layout as the content
		// view
		mLinearLayout.addView(i);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("RunGameActivity", "OnTouchEvent");
		synchronized (this) {
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background image. Operations on the Canvas accumulate
		// so this is like clearing the screen.
		// canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		super.onDraw(canvas);
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

		// COTE GAUCHE
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(200, 0);
		path.lineTo(0, 300);
		path.close();
		path.offset(0, 0);
		canvas.drawPath(path, p);

		Path triangle = new Path();
		triangle.moveTo(0, 300);
		triangle.lineTo(200, 500);
		triangle.lineTo(0, 1000);
		triangle.close();
		triangle.offset(0, 0);
		canvas.drawPath(triangle, p);

		Path triangle2 = new Path();
		triangle2.moveTo(0, 1000);
		triangle2.lineTo(200, 1200);
		triangle2.lineTo(0, 1280);
		triangle2.close();
		triangle2.offset(0, 0);
		canvas.drawPath(triangle2, p);

		// A DROITE
		Path path2 = new Path();
		path2.moveTo(800, 0);
		path2.lineTo(600, 0);
		path2.lineTo(800, 500);
		path2.close();
		path2.offset(0, 0);
		canvas.drawPath(path2, p);

		Path path3 = new Path();
		path3.moveTo(800, 500);
		path3.lineTo(600, 900);
		path3.lineTo(800, 1280);
		path3.close();
		path3.offset(0, 0);
		canvas.drawPath(path3, p);

		
		Bitmap car = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		canvas.drawBitmap(car, 360, 1100, null);

	}

}
