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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameOverActivity extends Activity {
	private int			explosionId;
	private SoundPool	soundPool;
	private boolean		loaded	= false;
	private RelativeLayout	layout		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (RelativeLayout) RelativeLayout.inflate(this,
				R.layout.activity_game_over, null);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		loadSettings();

		/*
		 * SON de gameOver ?
		 */
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Chargement du fichier musique.mp3 qui se trouve sous assets de notre

		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		explosionId = soundPool.load(this, R.drawable.bip, 1);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;

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
		
		setContentView(layout);
		//playSound(R.drawable.game_over_song);
	}
	private void playSound(int resId) {
		if (loaded) {
			soundPool.play(explosionId, (float)0.5, (float)0.5, 0, 0, 1);
		}
	}
	/**
	 * Chargement de la couleur du thème choisi pour la couleur de fond du titre
	 */
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
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
