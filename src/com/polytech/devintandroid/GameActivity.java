package com.polytech.devintandroid;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameActivity extends Activity implements SensorEventListener{
	private SensorManager	sensorManager;
	private Sensor			accelerometer;
	//private Intent			intent;
	private TextView		view_x, view_y, view_z;
	private float	x, y, z;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		gameActivityInit();
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		
		setContentView(R.layout.activity_game);
		
		
		view_x = (TextView) findViewById(R.id.textpos_x);
		view_y = (TextView) findViewById(R.id.textpos_y);
		view_z = (TextView) findViewById(R.id.textpos_z);
		
		
		
		//intent = getIntent();
	}
	
	public void gameActivityInit() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
	@Override
	protected void onPause() {
		// unregister the sensor (désenregistrer le capteur)
		sensorManager.unregisterListener((SensorEventListener) this, accelerometer);
		super.onPause();
	}

	@Override
	protected void onResume() {
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
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
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.x = event.values[0];
			this.y = event.values[1];
			this.z = event.values[2];
		}
		Log.d("Log.INFO", "Sensor's values ("+x+","+y+","+z+")");
		view_x.setText(""+this.getX());
		view_y.setText(""+this.getY());
		view_z.setText(""+this.getZ());
	}

}
