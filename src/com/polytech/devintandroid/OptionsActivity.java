package com.polytech.devintandroid;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class OptionsActivity extends Activity {
	// THEMES
	public static final int	THEME_BLEU	= 0;
	public static final int	THEME_ROUGE	= 1;
	// CARS
	public static final int	RED_CAR		= 0;
	public static final int	POLICE_CAR	= 1;
	public static final int	BLUE_CAR	= 2;
	public static final int	GREEN_CAR	= 3;

	// LEVELS
	public static final int	FACILE		= 0;
	public static final int	NORMAL		= 1;
	public static final int	DIFFICILE	= 2;
	public static final int	HARDCORE	= 3;
	// SPINNERS
	private Spinner			themeSpinner, carSpinner, levelSpinner;
	private CheckBox		boostCheck, sonCheck;
	private boolean			boostc,sonc;
	private LinearLayout	layout		= null;
	private int				posTheme, posCar, posLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_options, null);
		loadSettings();
		setContentView(layout);

		themeSpinner = (Spinner) findViewById(R.id.selectionTheme);
		themeSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.choixTheme, R.layout.spinner_theme));
		Log.d("debug setelection", "setselection " + posCar);
		themeSpinner.setSelection(this.posTheme);

		themeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				posTheme = position;
				saveSettings();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d("Nothing selected", "Nothing selected");
			}

		});

		carSpinner = (Spinner) findViewById(R.id.selectionCar);
		carSpinner.setAdapter(new MyCustomAdapter(OptionsActivity.this,
				R.layout.spinner_item, getResources().getStringArray(
						R.array.choixCar)));
		carSpinner.setSelection(this.posCar);

		carSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				posCar = position;
				saveSettings();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d("here", "here");
			}

		});

		levelSpinner = (Spinner) findViewById(R.id.selectionDifficulte);
		levelSpinner.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.choixdifficulte, R.layout.spinner_theme));
		levelSpinner.setSelection(this.posLevel);

		levelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				posLevel = position;
				saveSettings();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d("Nothing selected", "Nothing selected");
			}

		});

		boostCheck = (CheckBox) findViewById(R.id.boostCheck);
		boostCheck.setChecked(this.boostc);
		boostCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					boostc = ((CheckBox) v).isChecked();
					saveSettings();
			}
		});
		sonCheck = (CheckBox) findViewById(R.id.son);
		sonCheck.setChecked(this.sonc);
		sonCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sonc = ((CheckBox) v).isChecked();
				saveSettings();
				
			}
		});
		/*
		 * Ajout du listener sur le bouton retour pour revenir à MainActivity
		 */
		Button backOption = (Button) findViewById(R.id.backOptions);
		backOption.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent main = new Intent(OptionsActivity.this,
						MainActivity.class);
				startActivity(main);
			}
		});
	}

	/**
	 * Chargement de la couleur du thème choisi pour la couleur de fond du titre
	 */
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		TextView titre = (TextView) layout.findViewById(R.id.texthelp);
		this.posTheme = settings.getInt("titreFond", 0);
		switch (this.posTheme) {

		case OptionsActivity.THEME_BLEU:
			titre.setBackgroundColor(Color.parseColor("#0000FF"));
			break;
		case OptionsActivity.THEME_ROUGE:
			titre.setBackgroundColor(Color.parseColor("#FF0000"));
			break;
		default:
			titre.setBackgroundColor(Color.parseColor("#0000FF"));

		}
		this.posCar = settings.getInt("car", 0);
		this.posLevel = settings.getInt("level", 0);
		this.boostc = settings.getBoolean("boost", false);
		this.sonc = settings.getBoolean("son", false);
		

	}

	public void saveSettings() {
		SharedPreferences settings = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("titreFond", posTheme);
		editor.putInt("car", posCar);
		editor.putInt("level", posLevel);
		editor.putBoolean("boost", boostc);
		editor.putBoolean("son", sonc);
		editor.commit();
	}

	/**
	 * 
	 * @author Fabien Pinel Custom adapter pour le spinner contenant les
	 *         voitures
	 * 
	 */
	public class MyCustomAdapter extends ArrayAdapter<String> {

		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			String[] donnees = getResources().getStringArray(R.array.choixCar);
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_item, parent, false);
			TextView label = (TextView) row.findViewById(R.id.tvcar);
			label.setText(donnees[position]);

			ImageView icon = (ImageView) row.findViewById(R.id.car);

			switch (position) {
			case RED_CAR:
				icon.setImageResource(R.drawable.redcar);
				break;
			case BLUE_CAR:
				icon.setImageResource(R.drawable.bleu);
				break;
			case POLICE_CAR:
				icon.setImageResource(R.drawable.police);
				break;
			case GREEN_CAR:
				icon.setImageResource(R.drawable.vert);
				break;
			default:
				icon.setImageResource(R.drawable.bleu);
			}

			return row;
		}
	}
}