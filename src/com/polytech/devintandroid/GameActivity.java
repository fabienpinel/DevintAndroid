package com.polytech.devintandroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class GameActivity extends Activity implements SensorEventListener,
		KeyListener {

	private SensorManager	sensorManager;
	private Sensor			accelerometer;
	private float			x, y, z;
	private Display			display;
	private Vue				vue;
	private static int		majoration	= 2;
	private LinearLayout	layout		= null;
	private int				car, level;
	boolean					soundReady	= false;
	private Canvas			canvas;
	private boolean			boost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayout(((LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_game, null)));

		// empecher la mise en veille de l'écran
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/*
		 * Lecture de fichier son
		 */

		loadSettings();
		vue = new Vue(this, car, level);
		gameActivityInit();
		Log.d("init", "init");

		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(vue);

	}

	public void gameActivityInit() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Lorsque l'application s'arrête, il faut arrêter proprement la boucle de
	 * jeu
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		if (event != null && display != null) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				switch (display.getRotation()) {
				case Surface.ROTATION_0:
					this.x = event.values[0];
					this.y = event.values[1];
					break;
				case Surface.ROTATION_90:
					this.x = -event.values[1];
					this.y = event.values[0];
					break;
				case Surface.ROTATION_180:
					this.x = -event.values[0];
					this.y = -event.values[1];
					break;
				case Surface.ROTATION_270:
					this.x = event.values[1];
					this.y = -event.values[0];
					break;
				}
				this.z = event.values[2];

				x *= majoration;

				vue.game.setOrientationGap((int) Math.round(x));
				// vue.game.addOrientationGap((int) Math.round(x));

			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (level == OptionsActivity.FACILE) {
			return true;
		}
		if (boost) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				this.vue.speedBoostOnTouch();
			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				this.vue.speedBoostOnRelease();
			}
		}
		return true;
	}

	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		this.car = settings.getInt("car", 0);
		this.level = settings.getInt("level", OptionsActivity.NORMAL);
		this.boost = settings.getBoolean("boost", false);
	}

	public LinearLayout getLayout() {
		return layout;
	}

	public void setLayout(LinearLayout layout) {
		this.layout = layout;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void clearMetaKeyState(View view, Editable content, int states) {
	}

	@Override
	public int getInputType() {
		return 0;
	}

	@Override
	public boolean onKeyDown(View view, Editable text, int keyCode,
			KeyEvent event) {

		return true;
	}

	@Override
	public boolean onKeyOther(View view, Editable text, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return true;
		/*
		 * int keyCode = event.getKeyCode(); switch (keyCode) { case
		 * KeyEvent.KEYCODE_VOLUME_UP: vue.game.addOrientationGap(50); return
		 * true; case KeyEvent.KEYCODE_VOLUME_DOWN:
		 * vue.game.addOrientationGap(-50); return true; default: return
		 * super.dispatchKeyEvent(event); }
		 */
	}
}
