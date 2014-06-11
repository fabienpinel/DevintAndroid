package com.polytech.devintandroid;


import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class GameOverActivity extends Activity {
	private int				explosionId;
	private SoundPool		soundPool;
	private boolean			loaded			= false;
	private LinearLayout	layout			= null;
	private int				currentScore	= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_game_over, null);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		loadSettings();

		// playThisText("GAME OVER");
		/*
		 * SON de gameOver ?
		 */
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Chargement du fichier musique.mp3 qui se trouve sous assets de notre

		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

		explosionId = soundPool.load(this, R.drawable.game_over, 1);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
				playSound(explosionId);

			}
		});
		/*
		 * Ajout du listener sur le bouton rejouer pour revenir à GameActivity
		 */
		Button playAgain = (Button) layout.findViewById(R.id.playAgain);
		playAgain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent main = new Intent(GameOverActivity.this,
						GameActivity.class);
				startActivity(main);
			}
		});
		/*
		 * Ajout du listener sur le bouton menu pour revenir au menu
		 */
		Button menu = (Button) layout.findViewById(R.id.menu);
		menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent menuIntent = new Intent(GameOverActivity.this,
						MainActivity.class);
				startActivity(menuIntent);
			}
		});

		setContentView(layout);
		// playSound(R.drawable.game_over_song);
	}

	private void playSound(int resId) {
		if (loaded) {
			soundPool.play(resId, (float) 2, (float) 2, 0, 0, 1);
		}
	}

	/**
	 * Chargement de la couleur du thème choisi pour la couleur de fond du titre
	 */
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		this.currentScore = settings.getInt("currentScore", 0);
		TextView score = (TextView) layout.findViewById(R.id.gameOverScore);
		score.setText("Ton score : " + this.currentScore);
		TextView titre = (TextView) layout.findViewById(R.id.gameOverTitle);
		switch (settings.getInt("titreFond", 0)) {
		case OptionsActivity.THEME_BLEU:
			titre.setTextColor(Color.parseColor("#0000FF"));
			break;
		case OptionsActivity.THEME_ROUGE:
			titre.setTextColor(Color.parseColor("#FF0000"));
			break;
		default:
			titre.setTextColor(Color.parseColor("#0000FF"));

		}

	}

}
