package com.polytech.devintandroid;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class HelpActivity extends Activity implements OnInitListener {
	private LinearLayout	layout	= null;
	private TextView		helptext;
	private TextToSpeech	mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_help, null);

		loadSettings();
		setContentView(layout);
		init();

		helptext = (TextView) findViewById(R.id.texthelp);
		Button playHelpButton = (Button) layout
				.findViewById(R.id.playHelpButton);
		playHelpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playThisText(helptext.getText().toString());

			}
		});

	}

	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		TextView titre = (TextView) layout.findViewById(R.id.titleHelp);
		switch (settings.getInt("titreFond", 0)) {

		case OptionsActivity.THEME_BLEU:
			titre.setBackgroundColor(Color.parseColor("#0000FF"));
			break;
		case OptionsActivity.THEME_ROUGE:
			titre.setBackgroundColor(Color.parseColor("#FF0000"));
			break;
		default:
			titre.setBackgroundColor(Color.parseColor("#0000FF"));

		}
	}

	public void init() {
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, 0x01);

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
