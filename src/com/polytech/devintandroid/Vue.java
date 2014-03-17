package com.polytech.devintandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
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
	protected void onDraw(Canvas canvas) {
		// Draw the background image. Operations on the Canvas accumulate
		// so this is like clearing the screen.
		// canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		super.onDraw(canvas);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		// Draw the shadow
		canvas.drawLine(0, 10, 10, 20, p);
		canvas.drawRect(10, 200, 150, 350, p);
	}

	

}
