package com.polytech.devintandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

public class HelpActivity extends Activity {
	LinearLayout	layout	= null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		layout = (LinearLayout) LinearLayout.inflate(this,
				R.layout.activity_help, null);
		loadSettings();
		setContentView(layout);
		Intent intent = getIntent();
	}
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences("prefs", Context.MODE_PRIVATE);
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
	

}
