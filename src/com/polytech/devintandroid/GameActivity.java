package com.polytech.devintandroid;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameActivity extends Activity  {
	private SensorManager	sensorManager;
	private Sensor			accelerometer;
	// private Intent intent;
	private TextView		view_x, view_y, view_z;
	private float			x, y, z;
	private Display			display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameActivityInit();
		/*sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);*/
		GLSurfaceView view = new GLSurfaceView(this);

		view.setRenderer(new Draw(this, null));
		setContentView(view);
		// setContentView(R.layout.activity_game);
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		view_x = (TextView) findViewById(R.id.textpos_x);
		view_y = (TextView) findViewById(R.id.textpos_y);
		view_z = (TextView) findViewById(R.id.textpos_z);

		// intent = getIntent();
	}

	public void gameActivityInit() {
		/*sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);*/
	}

	@Override
	protected void onPause() {
		// unregister the sensor (désenregistrer le capteur)
		//sensorManager.unregisterListener((SensorEventListener) this,accelerometer);
		super.onPause();
	}

	@Override
	protected void onResume() {
		//sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_UI);
		super.onResume();
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
		// TODO Auto-generated method stub

	}

	/*public void onSensorChanged(SensorEvent event) {
		if (event != null && display != null) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// En fonction de l'orientation de l'appareil, on corrige les
				// valeurs x et y du capteur
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
				// la valeur de z
				this.z = event.values[2];
				view_x.setText("x: " + this.getX());
				view_y.setText("y: " + this.getY());
				view_z.setText("z: " + this.getZ());
			}
		}
		// Log.d("Log.INFO", "Sensor's values (" + x + "," + y + "," + z + ")");

	}*/

	
}
