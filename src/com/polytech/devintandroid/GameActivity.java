package com.polytech.devintandroid;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameActivity extends Activity implements SensorEventListener {
	private SensorManager	sensorManager;
	private Sensor			accelerometer;
	// private TextView view_x, view_y, view_z;
	private float			x, y, z;
	private Display			display;
	private Vue				vue;
	private static int		majoration	= 6;
	private LinearLayout	layout		= null;
	private int				car;
	private int				explosionId;
	private SoundPool		soundPool;
	boolean					soundReady	= false;
	Canvas					canvas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		layout = ((LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_game, null));

		/*
		 * Lecture de fichier son
		 */
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		AssetManager assetManager = getAssets();
		AssetFileDescriptor descriptor = null;
        try {
        	// Chargement du fichier musique.mp3 qui se trouve sous assets de notre projet
			descriptor = assetManager.openFd("file:///res/raw/songs/bip.ogg");
			
			MediaPlayer mediaPlayer = new MediaPlayer ();
			
			Log.d("testMediaPlayer", "setDataSource du mediaPlayer" + descriptor);
			mediaPlayer.setDataSource(descriptor.getFileDescriptor());

			// Listener pour être prévenu lorsque la musique se termine
			mediaPlayer.setOnCompletionListener(new OnCompletionListener()
			{
				// Libérons les ressources lorsque la musique est terminée
				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					// TODO Auto-generated method stub
					Log.d("testMediaPlayer", "etat:completed");
					Log.d("testMediaPlayer", "release en cours...");
					mediaPlayer.release();
				}
			});
			
			// Listener afin de lancer la musique lors le mediaPlayer est prêt
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					Log.d("testMediaPlayer", "etat:prepared");
					Log.d("testMediaPlayer", "Zou, jouons la musique !");
					mediaPlayer.start();
				}
			});

			Log.d("testMediaPlayer", "préparation du mediaPlayer");
			mediaPlayer.prepareAsync(); // Lancement de la préparation du mediaPlayer...
			

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
		
		
		
		
		
		loadSettings();
		vue = new Vue(this, car);
		gameActivityInit();
		Log.d("init", "init");

		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		
		/*
		 * Fin lecture de fichier son
		 */
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
				if (x > 0) {
					x *= majoration;
				} else {
					x *= majoration;
				}

				vue.game.updateOrientation((int) Math.round(x));
				soundPool.play(explosionId, 1, 1, 0, 0, 1);
			}
			/*
			 * view_x.setText("x: " + this.getX()); view_y.setText("y: " +
			 * this.getY()); view_z.setText("z: " + this.getZ());
			 */
		}

	}

	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		this.car = settings.getInt("car", 0);
	}

}
