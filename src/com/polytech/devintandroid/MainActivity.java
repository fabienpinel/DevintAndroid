package com.polytech.devintandroid;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class MainActivity extends Activity{
	LinearLayout	layout	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_main, null);
		loadSettings();
		setContentView(layout);

		/*
		 * Ajout du listener sur le bouton start pour charger l'activité
		 * StartActivity
		 */
		Button startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent gameView = new Intent(MainActivity.this,
						GameActivity.class);
				startActivity(gameView);
			}
		});
		
		/*
		 * Ajout du listener sur le bouton Options pour charger l'activité
		 * OptionsActivity
		 */
		Button optionsButton = (Button) findViewById(R.id.optionsButton);
		optionsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent optionsView = new Intent(MainActivity.this,
						OptionsActivity.class);
				startActivity(optionsView);
			}
		});
		/*
		 * Ajout du listener sur le bouton Help pour charger l'activité
		 * HelpActivity
		 */
		Button helpButton = (Button) findViewById(R.id.helpButton);
		helpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent helpView = new Intent(MainActivity.this,
						HelpActivity.class);
				startActivity(helpView);
			}
		});

	}
	/**
	 * Chargement des options et notamment du thème
	 */
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		TextView titre = (TextView) layout.findViewById(R.id.title);
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

}
