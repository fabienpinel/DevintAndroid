package com.polytech.devintandroid;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameActivity extends Activity implements SensorEventListener {
	private SensorManager	sensorManager;
	private Sensor			accelerometer;
	// private Intent intent;
	private TextView		view_x, view_y, view_z;
	private float			x, y, z;
	private Display			display;
	Vue						vue;
	private int majoration = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vue = new Vue(this);
       
		//vue = new Vue(this, null);
		gameActivityInit();
		Log.d("init","init");
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);

		// setContentView(R.layout.activity_game);
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		/*view_x = (TextView) findViewById(R.id.textpos_x);
		view_y = (TextView) findViewById(R.id.textpos_y);
		view_z = (TextView) findViewById(R.id.textpos_z);
*/
		//setContentView(vue);
		Log.d("avant setcontentview", "avant setcontentview");
		 setContentView(vue);
		 Log.d("apres setcontentview", "apres setcontentview");
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
	/** Lorsque l'application s'arrête, 
	   il faut arrêter proprement la boucle de jeu*/
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
		// TODO Auto-generated method stub

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
				if(x>0){
					x+=majoration;
				}else{
					x-=majoration;
				}
				vue.game.updateOrientation((int)x);
				//Log.d("x: "+x+" y: "+y+" z: "+z, "x: "+x+" y: "+y+" z: "+z);
				/*view_x.setText("x: " + this.getX());
				view_y.setText("y: " + this.getY());
				view_z.setText("z: " + this.getZ());*/
			}
		}

	}

}
