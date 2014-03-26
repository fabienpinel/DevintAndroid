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
	//CARS
	public static final int RED_CAR=0;
	public static final int POLICE_CAR=1;
	public static final int BLUE_CAR=2;
	public static final int GREEN_CAR=3;
	
	Spinner					themeSpinner;
	Spinner					carSpinner;
	LinearLayout	layout	= null;
	int pos, posCar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_options, null);
		loadSettings();
		setContentView(layout);
		Intent intent = getIntent();

		themeSpinner = (Spinner) findViewById(R.id.selectionTheme);
		themeSpinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.choixTheme, R.layout.spinner_item));

		themeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
					pos=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d("here", "here");
			}

		});
		
		carSpinner = (Spinner) findViewById(R.id.selectionCar);
		carSpinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.choixCar, R.layout.spinner_item));

		carSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
					posCar=position;
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
				SharedPreferences settings = getSharedPreferences("prefs",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("titreFond", pos);
				editor.putInt("car", posCar);
				editor.commit();
				Intent main = new Intent(OptionsActivity.this,
						MainActivity.class);
				startActivity(main);
			}
		});
		Button backOption = (Button) findViewById(R.id.backOptions);
		backOption.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent main = new Intent(OptionsActivity.this,
						MainActivity.class);
				startActivity(main);
			}
		});
	}
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs", Context.MODE_PRIVATE);
		TextView titre = (TextView) layout.findViewById(R.id.textView1);
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