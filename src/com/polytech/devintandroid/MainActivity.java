package com.polytech.devintandroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

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
	 * Pour l'instant ce menu ne sert pas.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
