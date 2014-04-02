package com.polytech.devintAndroid.voice;

import java.util.Locale;

import com.polytech.devintandroid.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class VoiceActivity extends Activity implements OnInitListener {
	private TextToSpeech	mTts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);
		init();
		Button playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				play("coucou !");
				
			}
		});

	}
	public void init(){
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
				mTts.setSpeechRate(1); // 1 est la valeur par défaut. Une valeur
										// inférieure rendra l'énonciation plus
										// lente, une valeur supérieure la
										// rendra plus rapide.
				mTts.setPitch(1); // 1 est la valeur par défaut. Une valeur
									// inférieure rendra l'énonciation plus
									// grave, une valeur supérieure la rendra
									// plus aigue.
			} else {
				// Echec, aucun moteur n'a été trouvé, on propose à
				// l'utilisateur d'en installer un depuis le Market
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Context context = getApplicationContext();
			CharSequence text = "TTS ready";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	public void play(String toPlay) {
		mTts.speak(toPlay, TextToSpeech.QUEUE_FLUSH, null);

	}

}
