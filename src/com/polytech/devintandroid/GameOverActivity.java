package com.polytech.devintandroid;

import java.util.Locale;

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
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author Fabien Pinel
 *
 */
public class GameOverActivity extends Activity implements OnInitListener {
	private int			explosionId;
	private SoundPool	soundPool;
	private boolean		loaded	= false;
	private RelativeLayout	layout		= null;
	private TextToSpeech	mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (RelativeLayout) RelativeLayout.inflate(this,
				R.layout.activity_game_over, null);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		loadSettings();
		this.TTSinit();
		
		//playThisText("GAME OVER");
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
		//playSound(R.drawable.game_over_song);
	}
	private void playSound(int resId) {
		if (loaded) {
			soundPool.play(explosionId, (float)0.5, (float)0.5, 0, 0, 1);
		}
	}
	public void TTSinit() {
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, 0x01);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x01) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// Succès, au moins un moteur de TTS à été trouvé, on
				// l'instancie
				mTts = new TextToSpeech(this, this);
				if (mTts.isLanguageAvailable(Locale.FRANCE) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
					mTts.setLanguage(Locale.FRANCE);
				}
				mTts.setSpeechRate(1);
				/*
				 * 1 est la valeur par défaut. Une valeurinférieure rendra
				 * l'énonciation plus lente, une valeur supérieure la rendra
				 * plus rapide.
				 */
				mTts.setPitch(1);
				/*
				 * 1 est la valeur par défaut. Une valeur inférieure rendra
				 * l'énonciation plus grave, une valeur supérieure la rendra
				 * plus aigue.
				 */
			} else {
				/*
				 * Echec, aucun moteur n'a été trouvé, on propose à
				 * l'utilisateur d'en installer un depuis le Market
				 */
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Toast toast = Toast.makeText(getApplicationContext(), "TTS ready",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void playThisText(String toPlay) {
		mTts.speak(toPlay, TextToSpeech.QUEUE_FLUSH, null);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.d("debug pause", "pause isSpeaking"+mTts.isSpeaking());
		if (mTts != null) {
			if (mTts.isSpeaking()) {

				Log.d("debug stopetShutdown", "stop+shutdown");
				mTts.stop();
				mTts.shutdown();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("debug destroy", "destroy isSpeaking" + mTts.isSpeaking());
		if (mTts.isSpeaking()) {
			Log.d("debug stopetShutdown", "stop+shutdown");
			mTts.stop();
			mTts.shutdown();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("debug stop", "stop isSpeaking" + mTts.isSpeaking());
		if (mTts.isSpeaking()) {
			Log.d("debug stopetShutdown", "stop+shutdown");
			mTts.stop();
			mTts.shutdown();
		}
	}
}
