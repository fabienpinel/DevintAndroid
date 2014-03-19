package com.polytech.devintandroid;

import android.content.Context;
import android.graphics.Bitmap;
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
		canvas.drawRect(0, 150, 150, 250, p);
		
		canvas.drawLine(0, 0, 100, 200, p);
		canvas.drawLine(100, 200, 50, 400, p);
		
		canvas.drawLine(50, 400, 100, 700, p);
		canvas.drawLine(100, 700, 20, 1000, p);
		
		canvas.drawLine(20, 1000, 300, 1280, p);
		
		canvas.drawLine(100, 0, 100, 1280, p);

		
	}

}
