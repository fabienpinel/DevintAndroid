package com.polytech.devintandroid;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

public class OptionsActivity extends Activity {
	public static final int	THEME_BLEU	= 0;
	public static final int	THEME_ROUGE	= 1;
	Spinner					themeSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		Intent intent = getIntent();

		themeSpinner = (Spinner) findViewById(R.id.selectionTheme);
		themeSpinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.choixTheme, R.layout.spinner_item));

		themeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
					SharedPreferences settings = getSharedPreferences("prefs",
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("titreFond", position);
					editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d("here", "here");
			}

		});

		/*
		 * Ajout du listener sur le bouton appliquer pour charger l'activité
		 * MainActivity
		 */
		Button applyOption = (Button) findViewById(R.id.applyOptions);
		applyOption.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent main = new Intent(OptionsActivity.this,
						MainActivity.class);
				startActivity(main);
			}
		});
	}
}